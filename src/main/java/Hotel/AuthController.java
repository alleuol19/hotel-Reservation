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
	
	@GetMapping("/book-room")
    public String bookingPage() {
        return "booking";
    }
	
	@GetMapping("/contact-us")
	public String contactUsPage() {
	    return "contact-us";
	}
	
	@PostMapping("/send-contact")
	public String sendContact(
	        @RequestParam String firstName,
	        @RequestParam String lastName,
	        @RequestParam String email,
	        @RequestParam String message
	) {

	    try {

	        Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

	        String sql = "INSERT INTO contact_messages " +
	                "(first_name, last_name, email, message) " +
	                "VALUES (?, ?, ?, ?)";

	        PreparedStatement stmt = conn.prepareStatement(sql);

	        stmt.setString(1, firstName);
	        stmt.setString(2, lastName);
	        stmt.setString(3, email);
	        stmt.setString(4, message);

	        stmt.executeUpdate();

	        stmt.close();
	        conn.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return "redirect:/";
	}

    @PostMapping("/save-booking")
    public String saveBooking(
            @RequestParam String fullName,
            @RequestParam String contactNumber,
            @RequestParam String email,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam String roomType,
            @RequestParam int guests,
            @RequestParam double price,
            Model model
    ) {
        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "INSERT INTO booking " +
                    "(full_name, contact_number, email, check_in_date, check_out_date, room_type, guests, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, fullName);
            stmt.setString(2, contactNumber);
            stmt.setString(3, email);
            stmt.setString(4, checkInDate);
            stmt.setString(5, checkOutDate);
            stmt.setString(6, roomType);
            stmt.setInt(7, guests);
            stmt.setDouble(8, price);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            model.addAttribute("message", "Booking successful!");
            return "homepage";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Booking failed. Please try again.");
            return "booking";
        }
    }
}