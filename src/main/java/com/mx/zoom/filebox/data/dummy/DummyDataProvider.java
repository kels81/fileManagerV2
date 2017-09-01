package com.mx.zoom.filebox.data.dummy;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import com.mx.zoom.filebox.data.DataProvider;
import com.mx.zoom.filebox.domain.DashboardNotification;
import com.mx.zoom.filebox.domain.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A dummy implementation for the backend API.
 */
public class DummyDataProvider implements DataProvider {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("file_manager");
    EntityManager em = emf.createEntityManager();

    /**
     * Initialize the data for this application.
     */
    public DummyDataProvider() {
        
    }

    @Override
    public User authenticate(String userName, String password) {
        User user = new User();
        user.setFirstName(DummyDataGenerator.randomFirstName());
        user.setLastName(DummyDataGenerator.randomLastName());
        user.setRole("admin");
        String email = user.getFirstName().toLowerCase() + "."
                + user.getLastName().toLowerCase() + "@"
                + DummyDataGenerator.randomCompanyName().toLowerCase() + ".com";
        user.setEmail(email.replaceAll(" ", ""));
        user.setLocation(DummyDataGenerator.randomWord(5, true));
        user.setBio("Quis aute iure reprehenderit in voluptate velit esse."
                + "Cras mattis iudicium purus sit amet fermentum.");
        return user;
    }
    
//    @Override
//    public Usuarios authenticate(String userName, String password) {
//        Consultas consultas = new Consultas();
//
//        ArrayList<Usuarios> listUsuarios = new ArrayList<Usuarios>();
//        listUsuarios = consultas.getUsuario(userName, password);
//        System.out.println("listUsuarios = " + listUsuarios);
//        
//        Usuarios usuario = new Usuarios();
//        
//        if (listUsuarios.isEmpty()) {
//            Notifications notification = new Notifications();
//            notification.createFailure("Favor de revisar los datos");
//        }else{
//            usuario = em.getReference(Usuarios.class, listUsuarios.get(0));
//        }
//        
//        //Usuarios usuario = new Usuarios();
//
////        for (Usuarios user : listUsuarios) {
////            usuario.setUNombre(user.getUNombre());
////            usuario.setUApellidoPaterno(user.getUApellidoPaterno());
////            usuario.setUApellidoMaterno(user.getUApellidoMaterno());
////            usuario.setuRole(user.getuRole());
////        }
//        
//
//        return usuario;
//    }

}
