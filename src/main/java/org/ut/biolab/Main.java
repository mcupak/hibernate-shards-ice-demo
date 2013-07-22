package org.ut.biolab;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class Main {
    public static void list() {
        SessionFactory sessionFactory = HibernateShardUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        // note: beware of this commented out query - it does implicit sorting
        // and many comparisons across shards - unusably slow
        // List<Variant> variantList =
        // session.createQuery("select p from Variant p").setMaxResults(10).list();

        Criteria crit = session.createCriteria(Variant.class);
        crit.add(Restrictions.lt("variant_id", 1));
        List<Variant> variantList = crit.list();

        Iterator<Variant> iterator = variantList.iterator();
        while (iterator.hasNext()) {
            Variant v = (Variant) iterator.next();
            System.out.println(v);
        }
        session.close();
    }

    public static void main(String[] args) {
        list();
    }
}
