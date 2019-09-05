package predicate;

import lombok.Data;

import java.util.function.Predicate;

@Data
public class Employee {
    
   public Employee(Integer id, Integer age, String gender, String fName, String lName){
       this.id = id;
       this.age = age;
       this.gender = gender;
       this.firstName = fName;
       this.lastName = lName;
   }
     
   private Integer id;
   private Integer age;
   private String gender;
   private String firstName;
   private String lastName;
 
   //Please generate Getter and Setters
 
   //To change body of generated methods, choose Tools | Templates.
    @Override
    public String toString() {
        return this.id.toString()+" - "+this.age.toString();
    }

    public static Predicate<Employee> isAdultMale() {
        return p -> p.getAge() > 21 && p.getGender().equalsIgnoreCase("M");
    }
}