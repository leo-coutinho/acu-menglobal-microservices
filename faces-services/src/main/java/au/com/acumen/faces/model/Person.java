package au.com.acumen.faces.model;

public class Person {
    private String personId;
    private String persistedFaceId;
    private String name;

    public Person() {
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersistedFaceId() {
        return persistedFaceId;
    }

    public void setPersistedFaceId(String persistedFaceId) {
        this.persistedFaceId = persistedFaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
