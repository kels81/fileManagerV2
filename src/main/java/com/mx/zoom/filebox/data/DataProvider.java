package com.mx.zoom.filebox.data;

import com.mx.zoom.filebox.domain.DashboardNotification;
import com.mx.zoom.filebox.domain.User;
import java.util.Collection;
import java.util.Date;


/**
 * QuickTickets Dashboard backend API.
 */
public interface DataProvider {

    /**
     * @param userName
     * @param password
     * @return Authenticated used.
     */
    User authenticate(String userName, String password);
   
}
