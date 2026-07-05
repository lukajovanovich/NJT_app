package projekat;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Luka
 */
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.class
})
@ComponentScan(basePackages = {"projekat", "projekat.kontroler", "projekat.servis", "projekat.repository"})
public class ProjectManagmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectManagmentApplication.class, args);
    }
}