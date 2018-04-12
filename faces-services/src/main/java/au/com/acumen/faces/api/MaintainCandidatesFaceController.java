package au.com.acumen.faces.api;


import au.com.acumen.faces.model.*;
import au.com.acumen.faces.repository.*;
import au.com.acumen.faces.utils.Base64EncodeDecode;
import au.com.acumen.faces.utils.CustomErrorType;
import au.com.acumen.faces.utils.MsImageApis;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import sun.misc.BASE64Decoder;

//import com.sun.xml.internal.messaging.saaj.util.Base64;

//import org.springframework.web.multipart.MultipartFile;


@Api
@RestController
//@EnableEurekaClient
@RequestMapping(path="/maintainCandidates")
public class MaintainCandidatesFaceController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MsImageApis msApis;

    @Autowired
    private Base64EncodeDecode base64EncodeDecode;
    @Autowired
    private CandidateFacesTableRepository candidateFacesTableRepository;

    @Autowired
    private CandidatesTableRepository candidatesTableRepository;

    @Autowired
    private MsCandidateFacesTableRepository msCandidateFacesTableRepository;

    @Autowired
    private MsCandidateRelationTableRepository msCandidateRelationTableRepository;

    @Autowired
    private DetectionTableRepository detectionTableRepository;

    @Autowired
    private CameraTableRepository camerasTableRepository;

    @Autowired
    private MsRecognitionTableRepository msRecognitionTableRepository;

    @Value("${spring.groupId}")
    private String GROUP_ID;



    @RequestMapping(method = RequestMethod.POST, path = "/addCandidates")
    @ApiOperation(value = "Add a Candidate locally and on MS Cloud for the Acu-Men Global Faces")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity addCandidates (@RequestParam(value = "title",required = false) String title,
                                         @RequestParam(value = "name") String name,
                                         @RequestParam(value = "gender") String gender,
                                         @RequestParam(value = "dob") String dob,
//                                         @RequestParam(value = "candidateImg") String candidateImg,
                                         @RequestPart(value = "candidateImg") MultipartFile candidateImg) {

        log.trace("Entering addCandidates() with {}", name);

//        BASE64Decoder decoder = new BASE64Decoder();
        BASE64Encoder encoder = new BASE64Encoder();
        String candImg = "";
//        InputStream inputStream = null;
//        try {
//            inputStream = candidateImg.getInputStream();
//
//        BufferedImage ImageFromConvert = ImageIO.read(inputStream);
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        ImageIO.write(ImageFromConvert, ".jpg", os);
//        BASE64Encoder encoder = new BASE64Encoder();
//        String imageString = encoder.encode(candidateImg.getBytes());
//
//        os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        byte[] image = new byte[0];
        try {
//            byte[] image1 = decoder.decodeBuffer(candidateImg);
              image = candidateImg.getBytes();
              candImg = encoder.encode(candidateImg.getBytes());
            //image = Base64.encodeBase64(candidateImg.getBytes());
//            byte[] image2 = decoder.decodeBuffer(String.valueOf(candidateImg));
        } catch (IOException e) {
            return new ResponseEntity(new CustomErrorType("Unable to read Image. IO Exception image " + e.getMessage() + " unable to read Image.")
                    , HttpStatus.BAD_REQUEST);

        }


        String personId = msApis.createPerson(name);
        if(personId == null) {
            log.trace("Unable to Create Person with MS Api createPerson. Candidate with Name {} not found.", name);
            return new ResponseEntity(new CustomErrorType("Unable to Create Person with MS Api createPerson. Candidate with Name " + name + " not found.")
                    ,HttpStatus.NOT_FOUND);
        }

//        String candImg = encoder.encode(candidateImg.getBytes());
        String candImg2 = Base64.encodeBase64URLSafeString(image) ;
        String persistedFaceId = msApis.addPersonFace(candImg,personId);
        if(persistedFaceId == null) {
            log.trace("Unable to Add Person Face with MS Api addPersonFace. Candidate with Image and PersonId {} not found.", personId);
            return new ResponseEntity(new CustomErrorType("Unable to Add Person Face with MS Api addPersonFace. Candidate with Image and PersonId " + personId + " could not be added.")
                    ,HttpStatus.NOT_FOUND);


        }
        //Go and train the Group for the new Candidate.
        msApis.trainPersonGroup();

        Candidates candidates = new Candidates();
        if((title != null) && (!title.isEmpty())) {
            candidates.setTitle(title);
        }else {
            candidates.setTitle("");
        }
        candidates.setName(name);
        candidates.setGender(gender);
        candidates.setDob(dob);
        candidatesTableRepository.saveAndFlush(candidates);


        MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
        msCandidateRelation.setCandidate(candidates);
        msCandidateRelation.setPersonId(personId);
        msCandidateRelationTableRepository.saveAndFlush(msCandidateRelation);


        CandidateFaces candidateFaces = new CandidateFaces();
        candidateFaces.setCandidate(candidates);
        candidateFaces.setCandidateImg(image);
        candidateFacesTableRepository.saveAndFlush(candidateFaces);

        MsCandidateFaces msCandidateFaces = new MsCandidateFaces();
        msCandidateFaces.setPersistedFaceId(persistedFaceId);
        msCandidateFaces.setCandidateFaces(candidateFaces);
        msCandidateFacesTableRepository.saveAndFlush(msCandidateFaces);


        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/addCandidateFaces") //,consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    @ApiOperation(value = "Add a Candidates Face locally and on MS Cloud for the Acu-Men Global Faces")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity addCandidateFaces (@RequestParam(value = "candidateId") Long candidateId,
                                             @RequestPart(value = "candidateImg") MultipartFile candidateImg) {

        log.trace("Entering addCandidateFaces() with {}", candidateId);

//        BASE64Decoder decoder = new BASE64Decoder();
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] image = new byte[0];
        String imag = "";
        try {
//            File img = candidateImg;
            image = candidateImg.getBytes();
            imag = encoder.encode(candidateImg.getBytes());

//            image = Base64.decodeBase64(candidateImg.getBytes());

        } catch (IOException e) {
            return new ResponseEntity(new CustomErrorType("Unable to read Image. IO Exception image " + e.getMessage() + " unable to read Image.")
                    , HttpStatus.BAD_REQUEST);

        }


        Candidates candidates = candidatesTableRepository.findOne(candidateId);

        if(candidates == null){
            return new ResponseEntity(new CustomErrorType("Unable to find Candidate with Id =  " + candidateId + " Please select an existing candidateId.")
                    , HttpStatus.BAD_REQUEST);

        }
        CandidateFaces candidateFaces = new CandidateFaces();
        candidateFaces.setCandidate(candidates);
        candidateFaces.setCandidateImg(image);
        candidateFacesTableRepository.saveAndFlush(candidateFaces);

        MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
        msCandidateRelation = msCandidateRelationTableRepository.findByCandidate(candidates);

        String persistedFaceId = msApis.addPersonFace(imag,msCandidateRelation.getPersonId());
        //Train the Group for the New Candidate face.
        msApis.trainPersonGroup();
        MsCandidateFaces msCandidateFaces = new MsCandidateFaces();
        msCandidateFaces.setPersistedFaceId(persistedFaceId);
        msCandidateFaces.setCandidateFaces(candidateFaces);
        msCandidateFacesTableRepository.saveAndFlush(msCandidateFaces);


        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/createGroupId") //,consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    @ApiOperation(value = "Creates a New GroupID on MS Cloud for the Acu-Men Global Faces")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity createGroupId () {

        log.trace("Entering createGroupId");


        int response = msApis.createGroupId();
        if(response != 200) {
            log.trace("Unable to create GroupID.  GROUP_ID must be entered at application.properties file. ", GROUP_ID);
            return new ResponseEntity(new CustomErrorType("Unable to create GROUPID. with GROUPID "
                    + GROUP_ID + " make sure GROUPID is on applications.property file.")
                    ,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/listAllGroups") //,consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    @ApiOperation(value = "Lists all existing GroupID's on MS Cloud for the Acu-Men Global Faces")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity<JSONArray> listAllGroups () {

        log.trace("Entering listAllGroups");


//        int response = msApis.createGroupId();
//        if(response != 200) {
//            log.trace("Unable to create GroupID.  GROUP_ID must be entered at application.properties file. ", GROUP_ID);
//            return new ResponseEntity(new CustomErrorType("Unable to create GROUPID. with GROUPID "
//                    + GROUP_ID + " make sure GROUPID is on applications.property file.")
//                    ,HttpStatus.NOT_FOUND);
//        }
        JSONArray jsonObArray = new JSONArray();
        jsonObArray = msApis.listPersonGroups();


        return ResponseEntity.ok(jsonObArray);
    }


    @RequestMapping(method = RequestMethod.GET,produces = "application/json",path = "/getCandidateProcessDetails")
    @ApiOperation(value = "Get Candidates Details for the Acu-Men Global Faces",response = CandidateProcessDetails.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity<CandidateProcessDetails> getCandidateProcessDetails (@RequestParam(value = "candidateId") Long candidateId) {

        log.trace("Entering addCandidateFaces() with {}", candidateId);

//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] image = new byte[0];
//        try {
////            File img = candidateImg;
//            image = decoder.decodeBuffer(candidateImg.toString());
//
//        } catch (IOException e) {
//            return new ResponseEntity(new CustomErrorType("Unable to read Image. IO Exception image " + e.getMessage() + " unable to read Image.")
//                    , HttpStatus.BAD_REQUEST);
//
//        }


        CandidateProcessDetails candidateProcessDetails = new CandidateProcessDetails();
        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        Gson gson = new GsonBuilder().create();

        Candidates candidates = candidatesTableRepository.findOne(candidateId);

        candidateProcessDetails.setCandidateId(candidateId);
        candidateProcessDetails.setName(candidates.getName());
        candidateProcessDetails.setGender(candidates.getGender());
        candidateProcessDetails.setDob(candidates.getDob());

        CandidateFaces candidateFaces = new CandidateFaces();
        candidateFaces = candidateFacesTableRepository.findByCandidate(candidates);
//        byte[] encodeBase64 = java.util.Base64.Encoder(candidateFaces.getCandidateImg());
        byte[] canImg = candidateFaces.getCandidateImg();
//       byte[] encodeBase64 = Base64.encode(canImg);

//        BASE64Encoder encoder = new BASE64Encoder();
//        ByteArrayInputStream bis = new ByteArrayInputStream(candidateFaces.getCandidateImg());
//        BufferedImage image = null;
        String base64Encoded = "";
        byte[] content = candidateFaces.getCandidateImg();
        String imageDataString = Base64.encodeBase64URLSafeString(content);
//        try {
//            base64Encoded = new String(candidateFaces.getCandidateImg(), "UTF-8");
////        response.setContentType("image/jpg");
////        response.setContentLength(content.length);
////        response.getOutputStream().write(content);
//
//            image = ImageIO.read(bis);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String imageString = encoder.encode(candidateFaces.getCandidateImg());
        candidateProcessDetails.setCandidateImage(Base64.encodeBase64String(canImg));


        MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
        msCandidateRelation = msCandidateRelationTableRepository.findByCandidate(candidates);

        String personId = msCandidateRelation.getPersonId();

        List<MsRecognition> msRecognition = new ArrayList<>();

        msRecognition = msRecognitionTableRepository.findByPersonId(personId);
        if(msRecognition == null) {
            log.trace("Unable to get Candidate Process Details. Candidate with PersonId  {} not yet Detectect or processed.", personId);
            return new ResponseEntity(new CustomErrorType("Unable to get Candidate Process Details. Candidate with PersonId " + personId + " not yet Detectect or processed.")
                    ,HttpStatus.NOT_FOUND);


        }
        Detection detection = new Detection();
        detection = detectionTableRepository.findOne(msRecognition.get(0).getDetection().getDetectionId());

        candidateProcessDetails.setConfidence(msRecognition.get(0).getConfidence());
        candidateProcessDetails.setDetectionDateTime(detection.getDetectionDateTime().toString());
        candidateProcessDetails.setDetectionImg(Base64.encodeBase64String(detection.getDetectionImg()));

        Cameras cameras = new Cameras();
        cameras = camerasTableRepository.findOne(detection.getCameras().getCameraId());

        candidateProcessDetails.setCameraAddress(cameras.getCameraAddress());
        candidateProcessDetails.setCameraLocation(cameras.getCameraLocation());
        candidateProcessDetails.setCameraName(cameras.getCameraName());
        String json = gson.toJson(candidateProcessDetails);// obj is your object

        return ResponseEntity.ok(candidateProcessDetails);
//        return json;
    }
//    @RequestMapping(method = RequestMethod.GET,produces = "application/json",path = "/getCandidateFlexProcessDetails")
//    @ApiOperation(value = "Get Candidates Details for the Acu-Men Global Faces",response = CandidateProcessDetails.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 404, message = "Service not available"),
//            @ApiResponse(code = 500, message = "Unexpected Runtime error") })
//
//    public Flux<CandidateProcessDetails> getCandidateFlexProcessDetails (@RequestParam(value = "candidateId") Long candidateId) {
//
//        log.trace("Entering addCandidateFaces() with {}", candidateId);
//
////        BASE64Decoder decoder = new BASE64Decoder();
////        byte[] image = new byte[0];
////        try {
//////            File img = candidateImg;
////            image = decoder.decodeBuffer(candidateImg.toString());
////
////        } catch (IOException e) {
////            return new ResponseEntity(new CustomErrorType("Unable to read Image. IO Exception image " + e.getMessage() + " unable to read Image.")
////                    , HttpStatus.BAD_REQUEST);
////
////        }
//
//
//        CandidateProcessDetails candidateProcessDetails = new CandidateProcessDetails();
//        ObjectMapper mapper = new ObjectMapper();
////        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//        Gson gson = new GsonBuilder().create();
//
//        Mono<Candidates> candidates = candidatesTableRepository.findOne();
////        Mono.justOrEmpty(user);
//         candidates.just(getCandidateFlexProcessDetails(candidateId));
//        candidateProcessDetails.setCandidateId(candidateId);
//        candidateProcessDetails.setName(candidates.just(0)..getName());
//        candidateProcessDetails.setGender(candidates.getGender());
//        candidateProcessDetails.setDob(candidates.getDob());
//
//        CandidateFaces candidateFaces = new CandidateFaces();
//        candidateFaces = candidateFacesTableRepository.findByCandidate(candidates);
////        byte[] encodeBase64 = java.util.Base64.Encoder(candidateFaces.getCandidateImg());
//        byte[] canImg = candidateFaces.getCandidateImg();
////       byte[] encodeBase64 = Base64.encode(canImg);
//
////        BASE64Encoder encoder = new BASE64Encoder();
////        ByteArrayInputStream bis = new ByteArrayInputStream(candidateFaces.getCandidateImg());
////        BufferedImage image = null;
//        String base64Encoded = "";
//        byte[] content = candidateFaces.getCandidateImg();
//        String imageDataString = Base64.encodeBase64URLSafeString(content);
////        try {
////            base64Encoded = new String(candidateFaces.getCandidateImg(), "UTF-8");
//////        response.setContentType("image/jpg");
//////        response.setContentLength(content.length);
//////        response.getOutputStream().write(content);
////
////            image = ImageIO.read(bis);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        String imageString = encoder.encode(candidateFaces.getCandidateImg());
//        candidateProcessDetails.setCandidateImage(Base64.encodeBase64String(canImg));
//
//
//        MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
//        msCandidateRelation = msCandidateRelationTableRepository.findByCandidate(candidates);
//
//        String personId = msCandidateRelation.getPersonId();
//
//        List<MsRecognition> msRecognition = new ArrayList<>();
//
//        msRecognition = msRecognitionTableRepository.findByPersonId(personId);
//        if(msRecognition == null) {
//            log.trace("Unable to get Candidate Process Details. Candidate with PersonId  {} not yet Detectect or processed.", personId);
//            return new ResponseEntity(new CustomErrorType("Unable to get Candidate Process Details. Candidate with PersonId " + personId + " not yet Detectect or processed.")
//                    ,HttpStatus.NOT_FOUND);
//
//
//        }
//        Detection detection = new Detection();
//        detection = detectionTableRepository.findOne(msRecognition.get(0).getDetection().getDetectionId());
//
//        candidateProcessDetails.setConfidence(msRecognition.get(0).getConfidence());
//        candidateProcessDetails.setDetectionDateTime(detection.getDetectionDateTime().toString());
//        candidateProcessDetails.setDetectionImg(Base64.encodeBase64String(detection.getDetectionImg()));
//
//        Cameras cameras = new Cameras();
//        cameras = camerasTableRepository.findOne(detection.getCameras().getCameraId());
//
//        candidateProcessDetails.setCameraAddress(cameras.getCameraAddress());
//        candidateProcessDetails.setCameraLocation(cameras.getCameraLocation());
//        candidateProcessDetails.setCameraName(cameras.getCameraName());
//        String json = gson.toJson(candidateProcessDetails);// obj is your object
//
//        return ResponseEntity.ok(candidateProcessDetails);
////        return json;
//    }

    @RequestMapping(method = RequestMethod.GET,produces = "application/json",path = "/getAllCandidates")
    @ApiOperation(value = "Get All Candidates for the Acu-Men Global Faces",response = Candidates.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity<List<Candidates>> getAllCandidates (@RequestParam(value = "candidateId",required = false) Long candidateId) {

        log.trace("Entering getAllCandidates() with {}", candidateId);

        Candidates candidates = new Candidates();
        List<Candidates> candidatesList = new ArrayList<>();

        if(candidateId != null) {
            candidates = candidatesTableRepository.findOne(candidateId);
            candidatesList.add(candidates);
        }else {
            candidatesList = candidatesTableRepository.findAll();
        }

        return ResponseEntity.ok(candidatesList);
    }
    @RequestMapping(method = RequestMethod.PUT,produces = "application/json",path = "/updateCandidate")
    @ApiOperation(value = "Update Candidate detailsfor the Acu-Men Global Faces")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity updateCandidate (@RequestParam(value = "candidateId",required = true) Long candidateId,
                                           @RequestParam(value = "dob",required = false) String dob,
                                           @RequestParam(value = "gender",required = false) String gender,
                                           @RequestParam(value = "name",required = false) String name,
                                           @RequestParam(value = "title",required = false) String title) {

        log.trace("Entering getAllCandidates() with {}", candidateId);

        Candidates candidates = new Candidates();
        List<Candidates> candidatesList = new ArrayList<>();
        boolean isUpdate = false;

        candidates = candidatesTableRepository.findOne(candidateId);
        if(dob != null) {
            candidates.setDob(dob);
            isUpdate = true;
        }
        if(gender != null) {
            candidates.setGender(gender);
            isUpdate = true;
        }
        if(name != null) {
            candidates.setName(name);
            isUpdate = true;
        }
        if(title != null) {
            candidates.setTitle(title);
            isUpdate = true;
        }
        if(isUpdate) {
            candidatesTableRepository.saveAndFlush(candidates);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET,produces = MediaType.IMAGE_JPEG_VALUE, path = "/getCandidateImageDetails")
    @ApiOperation(value = "Get Candidates Details for the Acu-Men Global Faces")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })


    public @ResponseBody byte[] getCandidateImageDetails (@RequestParam(value = "candidateId") Long candidateId) {

        log.trace("Entering getCandidateImageDetails with {}", candidateId);

//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] image = new byte[0];
//        try {
////            File img = candidateImg;
//            image = decoder.decodeBuffer(candidateImg.toString());
//
//        } catch (IOException e) {
//            return new ResponseEntity(new CustomErrorType("Unable to read Image. IO Exception image " + e.getMessage() + " unable to read Image.")
//                    , HttpStatus.BAD_REQUEST);
//
//        }


//        CandidateProcessDetails candidateProcessDetails = new CandidateProcessDetails();
//        ObjectMapper mapper = new ObjectMapper();
////        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
//        Gson gson = new GsonBuilder().create();

        Candidates candidates = candidatesTableRepository.findOne(candidateId);

//        candidateProcessDetails.setCandidateId(candidateId);
//        candidateProcessDetails.setName(candidates.getName());
//        candidateProcessDetails.setGender(candidates.getGender());
//        candidateProcessDetails.setDob(candidates.getDob());

        CandidateFaces candidateFaces = new CandidateFaces();
        candidateFaces = candidateFacesTableRepository.findByCandidate(candidates);
//        byte[] encodeBase64 = java.util.Base64.Encoder(candidateFaces.getCandidateImg());
//        byte[] canImg = candidateFaces.getCandidateImg();
//       byte[] encodeBase64 = Base64.encode(canImg);

//        BASE64Encoder encoder = new BASE64Encoder();
//        ByteArrayInputStream bis = new ByteArrayInputStream(candidateFaces.getCandidateImg());
//        BufferedImage image = null;
        String base64Encoded = "";
//        byte[] content = candidateFaces.getCandidateImg();
//        String imageDataString = Base64.encodeBase64URLSafeString(content);
//        try {
//            base64Encoded = new String(candidateFaces.getCandidateImg(), "UTF-8");
////        response.setContentType("image/jpg");
////        response.setContentLength(content.length);
////        response.getOutputStream().write(content);
//
//            image = ImageIO.read(bis);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String myImage = Base64.encodeBase64String(candidateFaces.getCandidateImg());
//        candidateProcessDetails.setCandidateImage(Base64.encodeBase64String(canImg));
//        BufferedImage bufferedImage = base64EncodeDecode.decodeToImage(myImage);

//        String newImg = base64EncodeDecode.encodeToString(bufferedImage,"JPG");

//        candidateProcessDetails.setDetectionImg(Base64.encodeBase64String(candidateFaces.getCandidateImg()));
//
//        MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
//        msCandidateRelation = msCandidateRelationTableRepository.findByCandidate(candidates);
//
//        String personId = msCandidateRelation.getPersonId();
//
//        MsRecognition msRecognition = new MsRecognition();
//
//        msRecognition = msRecognitionTableRepository.findByPersonId(personId);
//        Detection detection = new Detection();
//        detection = detectionTableRepository.findOne(msRecognition.getDetection().getDetectionId());
//
//        candidateProcessDetails.setConfidence(msRecognition.getConfidence());
//        candidateProcessDetails.setDetectionDateTime(detection.getDetectionDateTime().toString());
//        candidateProcessDetails.setDetectionImg(Base64.encodeBase64String(detection.getDetectionImg()));
//
//        Cameras cameras = new Cameras();
//        cameras = camerasTableRepository.findOne(detection.getCameras().getCameraId());
//
//        candidateProcessDetails.setCameraAddress(cameras.getCameraAddress());
//        candidateProcessDetails.setCameraLocation(cameras.getCameraLocation());
//        candidateProcessDetails.setCameraName(cameras.getCameraName());
//        String json = gson.toJson(candidateProcessDetails);// obj is your object
////        String myImage = Base64.encodeBase64String(detection.getDetectionImg());
////        String base64file = "";
////        try {
////            base64file = new String(Base64.encodeBase64(myImage.getBytes("UTF-8")));
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
////        Base64EncodeDecode base64EncodeDecode = new Base64EncodeDecode();
////        BufferedImage bufferedImage = base64EncodeDecode.decodeToImage(myImage);
////
////        String newImg = base64EncodeDecode.encodeToString(bufferedImage,"JPG");
////        return detection.getDetectionImg();
        return  candidateFaces.getCandidateImg();
//        return Base64.encodeBase64URLSafeString(detection.getDetectionImg());
    }

}