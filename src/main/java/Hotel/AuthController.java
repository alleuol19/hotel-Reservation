package Hotel;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;



@Controller
public class AuthController {

	static String url = "jdbc:mysql://mysql.railway.internal:3306/railway";
	static String dbUser = "root";
	static String dbPass = "qIOlaWsFbfipeyehbTLbiscGTAvfJyNv";
	
	@GetMapping("/")
	public String homepage() {
	    return "homepage";
	}
}