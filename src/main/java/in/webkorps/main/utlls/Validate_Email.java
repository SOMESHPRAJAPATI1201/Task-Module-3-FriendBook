package in.webkorps.main.utlls;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class Validate_Email {

    // Method to check if an email address is valid
    public static boolean isAddressValid(String email) {
        // Extract domain from email
        String domain = email.substring(email.indexOf('@') + 1);

        // Step 1: Get MX records for the domain
        ArrayList<String> mxRecords;
        try {
            mxRecords = getMXRecords(domain);
        } catch (NamingException e) {
            System.out.println("Domain lookup failed: " + e.getMessage());
            return false; // Domain doesn't exist
        }

        if (mxRecords.isEmpty()) {
            return false; // No MX records found, domain probably doesn't handle email
        }

        // Step 2: Try to connect to the mail server(s)
        for (String mx : mxRecords) {
            try {
                Socket socket = new Socket(mx, 25);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // Step 3: SMTP conversation
                if (hear(reader) != 220) throw new Exception("Invalid header from mail server");

                say(writer, "EHLO example.com"); // Use your domain here
                if (hear(reader) != 250) throw new Exception("SMTP not supported");

                // MAIL FROM (dummy sender)
                say(writer, "MAIL FROM:<test@example.com>");
                if (hear(reader) != 250) throw new Exception("Sender rejected");

                // RCPT TO (check the recipient email)
                say(writer, "RCPT TO:<" + email + ">");
                int response = hear(reader);

                // Close the connection politely
                say(writer, "QUIT");
                hear(reader);
                socket.close();

                // Step 4: If the mail server accepted the RCPT TO command (response 250), the email exists
                return response == 250;

            } catch (Exception e) {
                // Move to the next mail server in case of an error
                System.out.println("Error checking mail server: " + mx + " | " + e.getMessage());
            }
        }
        return false; // If no mail server accepted the email address
    }

    // Get MX records for a domain
    private static ArrayList<String> getMXRecords(String domain) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext dirContext = new InitialDirContext(env);
        Attributes attributes = dirContext.getAttributes(domain, new String[]{"MX"});
        Attribute mxAttr = attributes.get("MX");

        ArrayList<String> mxList = new ArrayList<>();
        if (mxAttr != null) {
            NamingEnumeration<?> servers = mxAttr.getAll();
            while (servers.hasMore()) {
                String mx = (String) servers.next();
                String[] parts = mx.split(" ");
                mxList.add(parts.length == 1 ? parts[0] : parts[1]); // Get the mail server name
            }
        }

        return mxList;
    }

    // Hear method to read responses from the server
    private static int hear(BufferedReader reader) throws IOException {
        String line;
        int res = 0;
        while ((line = reader.readLine()) != null) {
            try {
                res = Integer.parseInt(line.substring(0, 3));
            } catch (Exception e) {
                res = -1;
            }
            if (line.charAt(3) != '-') break;
        }
        return res;
    }

    // Send a command to the server
    private static void say(BufferedWriter writer, String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
    }

}

