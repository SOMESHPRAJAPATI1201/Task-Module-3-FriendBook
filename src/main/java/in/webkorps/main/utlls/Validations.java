package in.webkorps.main.utlls;


public class Validations {

    public static boolean email_validation(String email) {
        String regx = "[\\s]{0}[a-zA-Z0-9._%±]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,3}[\\s]{0,3}";
        return email.matches(regx);
    }

    public static boolean password_validation(String password) {
        String regex = "^[a-zA-Z0-9\\p{Punct}]{8,16}$";
        return password.matches(regex);
    }

    public static boolean name_validation(String name) {
        String regx = "[\\s]{0}[a-zA-Z]{1,20}+[\\s]{0,1}[a-zA-Z]{0,50}+[\\s]{0,1}[a-zA-Z]{0,50}[\\s]{0}";
        return name.matches(regx);
    }

    public static boolean address_validation(String address) {
        String ADDRESS_REGEX = "^(\\d+) ?([A-Za-z](?= ))? (.*?) ([^ ]+?) ?((?<= )APT)? ?((?<= )\\d*)?$";
        return address.matches(ADDRESS_REGEX);
    }

    public static boolean checkLoginCredentials(String email, String password ) {
        if (email_validation(email) && password_validation(password)) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean checkRegistrationCredentials(String email, String password, String name) {
        if (email_validation(email) && password_validation(password) && name_validation(name)) {
            return true;
        }else {
            return false;
        }
    }

    public static boolean checkRegistrationCredentials(String email, String password, String name, String address) {
        if (checkRegistrationCredentials(email, password,name) && address_validation(address)) {
            return true;
        }else {
            return false;
        }
    }

}
