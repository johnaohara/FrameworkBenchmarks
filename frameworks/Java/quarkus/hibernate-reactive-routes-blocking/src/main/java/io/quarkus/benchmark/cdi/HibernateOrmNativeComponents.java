package io.quarkus.benchmark.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;

import io.quarkus.runtime.StartupEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

@Singleton
public class HibernateOrmNativeComponents {

	@Inject
	EntityManagerFactory entityManagerFactory;

	private SessionFactory sessionFactory;

	void onStart(@Observes StartupEvent ev) {
		sessionFactory = entityManagerFactory.unwrap( SessionFactory.class );
	}

	public StatelessSession openStatelessSession() {
		return sessionFactory.openStatelessSession();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return sessionFactory.getCriteriaBuilder();
	}

}
