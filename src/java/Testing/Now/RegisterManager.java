/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing.Now;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
/**
 *
 * @author Rania
 */
@ManagedBean(name = "registerManager")
@SessionScoped
public class RegisterManager implements Serializable {
    private User user = null;
    private TrippleDes td;
    /**
     * Creates a new instance of RegisterManager
     */
    public RegisterManager() {
        user = new User();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }
    
    public String getFirstName() {
        return user.getFirstName();
    }

    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
    }
    
    public String getLastName() {
        return user.getLastName();
    }

    public void setLastName(String lastName) {
        user.setLastName(lastName);
    }
    
    public String getPassword() {
         return user.getPassword();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public String getConfirmPassword() {
         return user.getConfirmPassword();
    }

    public void setConfirmPassword(String confirmPassword) {
        user.setConfirmPassword(confirmPassword);
    }
    
    public String registerUser2() {
        try {
         boolean pass = processConfirm();
         if(pass){
            boolean fail = confirmUsername();
            if (fail) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Username already exists"));                
            }
            else {
                String encryptedPassword ="";
                try {
                   td = new TrippleDes();
                   encryptedPassword=td.encrypt(user.getPassword());
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                String next = insertTable( user.getEmail(), user.getFirstName(), user.getLastName(), user.getUsername(),encryptedPassword);
                user = new User();
                return next;
            }
         }
         else {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error, passwords must match"));
         }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
     }
    
     public static String insertTable(String email, String firstName, String lastName, String username, String password) throws Exception {
		try {
			Connection conn = getConnection();
                        createTable();
                        PreparedStatement posted = conn.prepareStatement("INSERT INTO users VALUES ('"+email+"', '"+firstName+"','"+lastName+"','"+username+"', '"+password+"')");
			//execute query we are receiving information, executeUpdate we are sending/adding information
			posted.executeUpdate();
                        //link used to redirect user to next page
                        return "/confirm.xhtml?faces-redirect=true";
                        
		}
		catch(Exception e) {
			e.printStackTrace();
                        if (e.getMessage().contains("Duplicate entry")) {
                            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Error, account already exists"));
                        }
		}
                return "";
	}
    public static Connection getConnection() throws Exception {
		try {
			String driver="com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/demo?verifyServerCertificate=false&useSSL=false&requireSSL=false";
			
                        //change according to your admin details
			String username = "root";
			String password = "root";
			
			Class.forName(driver);
			Connection conn=DriverManager.getConnection(url, username, password);
                        return conn;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public boolean processConfirm() {
        if(!user.getPassword().equalsIgnoreCase(user.getConfirmPassword())) {
            return false;
        }
        return true;
    }
    public boolean confirmUsername() throws Exception {
                boolean fail=false;
		try {
			Connection conn = getConnection();
			//to select all from table you say select * from newtable, but to select specific columns in table, you do as below
			PreparedStatement statement = conn.prepareStatement("SELECT (username) FROM users");
			
			ResultSet result=statement.executeQuery();
			
			
			
			while(result.next()) {
				if (user.getUsername().equalsIgnoreCase(result.getString("username"))){
                                    fail=true;
                                }
			}
			if(fail) {
                            return fail;
                        }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
   public void validateEmail(FacesContext context,UIComponent toValidate,Object value) { 
                String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	        Pattern p = Pattern.compile(regex);
	        Matcher matcher;
	        
                matcher = p.matcher((String)value);
	        if(!matcher.matches()) {
	            ((UIInput) toValidate).setValid(false);
                    FacesMessage message = new FacesMessage("Please enter a valid email");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                    context.addMessage(toValidate.getClientId(context), message);	
	        }
	        
     }
   
   public static void createTable() throws Exception {
		try {
			Connection conn = getConnection();
			PreparedStatement create  = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS demo");
                        create.executeUpdate();
                        create = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users(email varchar(255) PRIMARY KEY, firstname varchar(50), lastname varchar(50), username varchar(50), password varchar(255))");
			create.executeUpdate();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
