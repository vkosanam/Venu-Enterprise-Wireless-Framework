
package venuproject.uwf.edu;


import java.util.List;


/**
 * Data Access Object interface for the Person entity.
 * 
 * @author Venu Kosanam
 */
public interface PersonDao {
   void create(Person person);

   void update(Person person);

   void delete(Person person);

   List<String> getAllPersonNames();

   List<Person> findAll();

   Person findByPrimaryKey(String country, String company, String fullname);
}
