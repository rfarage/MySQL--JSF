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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
/**
 *
 * @author Rania
 */
@ManagedBean(name = "manager")
@SessionScoped
public class Manager implements Serializable {
    private String email;
    private String password;
    private String firstName;
    private TrippleDes td;
    /**
     * Creates a new instance of Manager
     */
    public Manager() {
        
    }
    
    public String getFirstName() {
        return this.firstName;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
       
    public String loginUser2() {
        try {
            String next = getAll();
            return next;
        }
        catch(Exception e) {
            
        }
        return null;
    }
    
    public String logoutUser() {
        email = "";
        password="";
        firstName="";
        return "/logins.xhtml?faces-redirect=true";
    }
    public static Connection getConnection() throws Exception {
		try {
			String driver="com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/demo?verifyServerCertificate=false&useSSL=false&requireSSL=false";
			
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
    
    public String getAll() throws Exception {
                boolean pass=false;
                td = new TrippleDes();
                try {
			Connection conn = getConnection();
			//to select all from table you say select * from newtable, but to select specific columns in table, you do as below
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
			
			ResultSet result=statement.executeQuery();
			
			
			boolean emailFound=false;
                        
                        while(result.next()) {
                            if(this.email.equalsIgnoreCase(result.getString("email"))) {
                                emailFound=true;
                                String oldPass=result.getString("password");
                                String decryptedPassword=td.decrypt(oldPass);
                                if (this.email.equalsIgnoreCase(result.getString("email")) && this.password.equalsIgnoreCase(decryptedPassword)){
                                    pass=true;
                                    this.firstName = result.getString("firstname");
                                }
                            }
			}
                        if(!emailFound) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("User does not exist, please register account"));
                            return "";
                        }
                        else if(!pass) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error, username or password is incorrect"));
                            return "";
                        }
                        else {
                            email="";
                            password="";
                           return "/user.xhtml?faces-redirect=true";
                        }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
               return "";
	}
}
