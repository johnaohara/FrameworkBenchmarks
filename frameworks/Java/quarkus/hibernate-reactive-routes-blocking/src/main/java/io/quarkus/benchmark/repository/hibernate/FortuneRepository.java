package io.quarkus.benchmark.repository.hibernate;

import io.quarkus.benchmark.cdi.HibernateOrmNativeComponents;
import io.quarkus.benchmark.model.hibernate.Fortune;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class FortuneRepository {

    @Inject
    HibernateOrmNativeComponents nativeComponents;

    public List<Fortune> findAllStateless() {
        try (StatelessSession s = nativeComponents.openStatelessSession()) {
            CriteriaBuilder criteriaBuilder = nativeComponents.getCriteriaBuilder();
            CriteriaQuery<Fortune> fortuneQuery = criteriaBuilder.createQuery(Fortune.class);
            Root<Fortune> from = fortuneQuery.from(Fortune.class);
            fortuneQuery.select(from);
            return s.createQuery(fortuneQuery).getResultList();
        }
    }
}
