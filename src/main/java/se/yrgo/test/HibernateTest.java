package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.util.List;

public class HibernateTest
{
	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

	public static void main(String[] args){
		setUpData();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<Student> results = em.createNamedQuery("searchByName", Student.class).setParameter("name", "Jimi Hendriks").getResultList();
		for(Student student: results) {
			System.out.println(student);
		}

		Query q = em.createQuery("select student.name from Student student");
		List<String>results2 = q.getResultList();
		for(String name:results2) {
			System.out.println(name);
		}

		List<Object[]>results3 = em.createQuery("select student.name, student.enrollmentID from Student student").getResultList();
		for(Object[] obj:results3) {
			System.out.println("Name: " + obj[0]);
			System.out.println("ID: " + obj[1]);
		}

		long numberOfStudents = (Long)em.createQuery("select count(student)from Student student").getSingleResult();
		System.out.println("We have " + numberOfStudents + " students");

		List<Object[]> results4 = em.createNativeQuery("select s.name,s.enrollmentid from student s").getResultList();
		for(Object[] result: results4) {
			System.out.println(result[0] + " ; " + result[1]);
		}

		List<Student>students = em.createNativeQuery("select * from student s", Student.class).getResultList();
		for(Student student: students) {
			System.out.println(student);
		}
		
		tx.commit();
		em.close();
	}

	public static void setUpData(){
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();


		Subject mathematics = new Subject("Mathematics", 2);
		Subject science = new Subject("Science", 2);
		Subject programming = new Subject("Programming", 3);
		em.persist(mathematics);
		em.persist(science);
		em.persist(programming);

		Tutor t1 = new Tutor("ABC123", "Johan Smith", 40000);
		t1.addSubjectsToTeach(mathematics);
		t1.addSubjectsToTeach(science);


		Tutor t2 = new Tutor("DEF456", "Sara Svensson", 20000);
		t2.addSubjectsToTeach(mathematics);
		t2.addSubjectsToTeach(science);

		// This tutor is the only tutor who can teach History
		Tutor t3 = new Tutor("GHI678", "Karin Lindberg", 0);
		t3.addSubjectsToTeach(programming);

		em.persist(t1);
		em.persist(t2);
		em.persist(t3);


		t1.createStudentAndAddtoTeachingGroup("Jimi Hendriks", "1-HEN-2019", "Street 1", "city 2", "1212");
		t1.createStudentAndAddtoTeachingGroup("Bruce Lee", "2-LEE-2019", "Street 2", "city 2", "2323");
		t3.createStudentAndAddtoTeachingGroup("Roger Waters", "3-WAT-2018", "Street 3", "city 3", "34343");

		tx.commit();
		em.close();
	}


}
