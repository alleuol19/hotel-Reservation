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
                    "(full_name, contact_number, email, check_in_date, check_out_date, room_type, guests, price, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, fullName);
            stmt.setString(2, contactNumber);
            stmt.setString(3, email);
            stmt.setString(4, checkInDate);
            stmt.setString(5, checkOutDate);
            stmt.setString(6, roomType);
            stmt.setInt(7, guests);
            stmt.setDouble(8, price);
            stmt.setString(9, "PENDING");

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
    @GetMapping("/front-desk")
    public String frontDesk(
            @RequestParam(defaultValue = "PENDING") String status,
            Model model
    ) {
        List<Map<String, Object>> bookings = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "SELECT * FROM booking WHERE status = ? ORDER BY id DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();

                booking.put("id", rs.getInt("id"));
                booking.put("fullName", rs.getString("full_name"));
                booking.put("contactNumber", rs.getString("contact_number"));
                booking.put("email", rs.getString("email"));
                booking.put("checkInDate", rs.getString("check_in_date"));
                booking.put("checkOutDate", rs.getString("check_out_date"));
                booking.put("roomType", rs.getString("room_type"));
                booking.put("guests", rs.getInt("guests"));
                booking.put("price", rs.getDouble("price"));
                booking.put("status", rs.getString("status"));
                booking.put("paymentMethod", rs.getString("payment_method"));
                booking.put("roomNumber", rs.getString("room_number"));
                String roomType = rs.getString("room_type");
                booking.put("availableRooms", getAvailableRooms(conn, roomType));

                bookings.add(booking);

            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("status", status);

        return "front-desk";
    }

    @PostMapping("/confirm-booking")
    public String confirmBooking(
            @RequestParam int id,
            @RequestParam String paymentMethod,
            @RequestParam String roomNumber
    ) {
        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "UPDATE booking SET status = 'SUCCESSFUL', payment_method = ?, room_number = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, paymentMethod);
            stmt.setString(2, roomNumber);
            stmt.setInt(3, id);

            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/front-desk?status=SUCCESSFUL";
    }

    @PostMapping("/cancel-booking")
    public String cancelBooking(@RequestParam int id) {
        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "UPDATE booking SET status = 'CANCELED' WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/front-desk?status=CANCELED";
    }

    @PostMapping("/delete-booking")
    public String deleteBooking(@RequestParam int id) {
        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String sql = "DELETE FROM booking WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/front-desk?status=CANCELED";
    }
    private List<String> getAvailableRooms(Connection conn, String roomType) {

        List<String> rooms = new ArrayList<>();

        int start = 101;
        int end = 110;

        if (roomType.equals("Economy Single")) {
            start = 101;
            end = 110;
        }
        else if (roomType.equals("Economy Double")) {
            start = 201;
            end = 210;
        }
        else if (roomType.equals("Economy Triple")) {
            start = 301;
            end = 310;
        }
        else if (roomType.equals("Normal Single")) {
            start = 401;
            end = 410;
        }
        else if (roomType.equals("Normal Double")) {
            start = 501;
            end = 510;
        }
        else if (roomType.equals("Normal Triple")) {
            start = 601;
            end = 610;
        }
        else if (roomType.equals("VIP Single")) {
            start = 701;
            end = 710;
        }
        else if (roomType.equals("VIP Double")) {
            start = 801;
            end = 810;
        }
        else if (roomType.equals("VIP Triple")) {
            start = 901;
            end = 910;
        }

        try {

            for (int i = start; i <= end; i++) {

                String roomNumber = "R" + i;

                String sql = "SELECT COUNT(*) FROM booking " +
                        "WHERE room_number = ? AND status = 'SUCCESSFUL'";

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, roomNumber);

                ResultSet rs = stmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    rooms.add(roomNumber);
                }

                rs.close();
                stmt.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }
    @PostMapping("/checkout-booking")
    public String checkoutBooking(@RequestParam int id) {

        try {
            Connection conn = DriverManager.getConnection(url, dbUser, dbPass);

            String insertSql =
                    "INSERT INTO checkout_records " +
                    "(booking_id, full_name, contact_number, email, check_in_date, check_out_date, room_type, guests, price, payment_method, room_number) " +
                    "SELECT id, full_name, contact_number, email, check_in_date, check_out_date, room_type, guests, price, payment_method, room_number " +
                    "FROM booking WHERE id = ?";

            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, id);
            insertStmt.executeUpdate();

            insertStmt.close();

            String deleteSql = "DELETE FROM booking WHERE id = ?";

            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();

            deleteStmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/front-desk?status=SUCCESSFUL";
    }
}