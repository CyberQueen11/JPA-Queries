package se.yrgo.test;

import jakarta.persistence.*;
import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.util.*;

public class HibernateTest {
	public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

	public static void main(String[] args) {
		setUpData();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		/*
		 * UPPGIFT 1
		 * Skriv en query för att få namnet på alla elever vars tutor kan undervisa i
		 * science.
		 */
		System.out.println("\n--Uppgift 1--");

		Subject science = em.find(Subject.class, 2);

		Query query = em.createQuery(
				"SELECT t.teachingGroup FROM Tutor t WHERE :subject MEMBER OF t.subjectsToTeach")
				.setParameter("subject", science);

		List<Student> students = query.getResultList();

		for (Student s : students) {
			System.out.println(s);
		}

		/*
		 * UPPGIFT 2
		 * Skriv en query för att hämta namnet på alla studenter och namnet på deras
		 * handledare(tutor)
		 */
		System.out.println("\n--Uppgift 2--");
		List<Object[]> studentNameAndTutor = em
				.createQuery(
						"SELECT s.name, t.name FROM Tutor t JOIN t.teachingGroup s", Object[].class)
				.getResultList();

		for (Object[] result : studentNameAndTutor) {
			String studentName = (String) result[0];
			String tutorName = (String) result[1];
			System.out.println("Student: " + studentName + ", Tutor: " + tutorName);
		}

		/*
		 * UPPGIFT 3
		 * Använd aggregation för att få den genomsnittliga termins längden (average
		 * semester) för ämnena(subjects).
		 */
		System.out.println("\n--Uppgift 3--");
		Double avgQuery = (Double) em.createQuery("SELECT AVG(s.numberOfSemesters) FROM Subject s").getSingleResult();
		System.out.println("Average semester: " + avgQuery);

		/*
		 * UPPGIFT 4
		 * Skriv en query som kan returnera max salary från tutor tabellen.
		 */
		System.out.println("\n--Uppgift 4--");
		TypedQuery<Integer> maxSalaryQuery = em.createQuery(
				"SELECT MAX(t.salary) FROM Tutor t",
				Integer.class);
		Integer maxSalary = maxSalaryQuery.getSingleResult();
		System.out.println("Max salary: " + maxSalary);

		/*
		 * UPPGIFT 5
		 * Skriv en named query som kan returnera alla tutor som har
		 * en lön högre än 10000.
		 */
		System.out.println("\n--Uppgift 5--");
		List<Tutor> tutorsSalary = em.createNamedQuery("tutorSalary", Tutor.class).getResultList();
		for (Tutor tutor : tutorsSalary) {
			System.out.println(
					"Tutor: " + tutor.getName() + " Salary: " + tutor.getSalary());
		}

		tx.commit();
		em.close();
	}

	public static void setUpData() {
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
