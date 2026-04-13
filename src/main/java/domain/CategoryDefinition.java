package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CategoryDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    private boolean approved;
    private String proposerEmail;
    private Date createdAt;

    public CategoryDefinition() {
    }

    public CategoryDefinition(String name, boolean approved, String proposerEmail) {
        this.name = name;
        this.approved = approved;
        this.proposerEmail = proposerEmail;
        this.createdAt = new Date();
    }

    public String getName() {
        return name;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getProposerEmail() {
        return proposerEmail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
