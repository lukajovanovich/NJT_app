package projekat.DTO;

import jakarta.validation.constraints.NotBlank;

public class PrijavaStatusDTO implements Dto {

    @NotBlank(message = "Status je obavezan.")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
