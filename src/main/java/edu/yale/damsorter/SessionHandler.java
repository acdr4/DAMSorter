/*
 *
 */
package edu.yale.damsorter;

import com.artesia.asset.Asset;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import com.artesia.event.services.EventServices;
import com.artesia.metadata.MetadataValue;
import com.artesia.security.SecuritySession;
import com.artesia.security.session.services.AuthenticationServices;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Main class for running examples.
 */
public class SessionHandler {

    static Logger log = Logger.getLogger(SessionHandler.class.getName());
    
    public static final int SLEEP_DURATION = 60000;

    public static SecuritySession getSecuritySession(String user, String password) throws BaseTeamsException {
        SecuritySession session = AuthenticationServices.getInstance().login(user, password);
        return session;
    }

    public static void logout(SecuritySession session) {
        try {
            AuthenticationServices.getInstance().logout(session);

        } catch (BaseTeamsException ex) {
            //Logger.getLogger(SessionHandler.class.getName()).log(Level.SEVERE, null, ex);
            log.info("Session logout not successful");
        }
    }

    public static boolean isValidSession(SecuritySession session) {
        if (session != null) {
            try {
                AuthenticationServices.getInstance().authenticateUserSession(session);
                return true;
            } catch (BaseTeamsException e) {
                logout(session);
            }
        }
        return false;
    }

    public static SecuritySession login(String userId, String password) {

        //give JVM info about location of directory with Tresource file
        //System.setProperty("TEAMS_HOME","C:\\MM7libs\\teams");

        //InetAddress ip = InetAddress.getLocalHost();
        //System.out.println("Current IP address : " + ip.getHostAddress());

        //Properties pr = System.getProperties();
        //pr.list(System.out);

        //RuntimeMXBean rtmxBean = ManagementFactory.getRuntimeMXBean();
        //List<String> arguments = rtmxBean.getInputArguments();
        //for(int i = 0; i < arguments.size(); i++)
        //    System.out.println(arguments.get(i));

        SecuritySession session = null;

        try {
            session = SessionHandler.getSecuritySession(userId, password);
        } catch (Exception e) {
            //System.out.println("DAM JNDI login failed");
            log.info("DAM JNDI login failed");
            System.exit(0);
        }

        log.info(" ");
        log.info("-------------------------------------------------------------------");
        log.info("logged in to JNDI as: " + session.getUserFullName());
        log.info("-------------------------------------------------------------------");
        log.info(" ");
        //System.out.println(" ");
        //System.out.println("-------------------------------------------------------------------");
        //System.out.println("logged in to JNDI as: " + session.getUserFullName());
        //System.out.println("-------------------------------------------------------------------");

        return session;        
    }
}
