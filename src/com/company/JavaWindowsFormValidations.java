package com.company;

public class JavaWindowsFormValidations {

    public static boolean validateOnlyTextBoxLetters(String controlName, String tbText, int minRanage, int maxRanage) {
        if (tbText == "" || tbText == null) {
            JavaWindowsFormUserInformers.showMsgWithJPane("Please enter " + controlName.toLowerCase() + ".");
            return false;
        }  else if (tbText.trim().length() < minRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too small.");
            return false;
        } else if (tbText.trim().length() > maxRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too long.");
            return false;
        }
        else if (tbText.chars().allMatch(x -> Character.isDigit(x) == true)) {

            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " should not contain digits.");
            return false;

        }
        else
            return true;
    }

    public static boolean validateOnlyTextBoxDigits(String controlName, String tbText, int minRanage, int maxRanage) {
        if (tbText == "" || tbText == null) {
            JavaWindowsFormUserInformers.showMsgWithJPane("Please enter " + controlName.toLowerCase() + ".");
            return false;
        } else if (tbText.trim().length() < minRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too small.");
            return false;
        } else if (tbText.trim().length() > maxRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too long.");
            return false;
        }
        else if (tbText.chars().allMatch(x -> Character.isLetter(x) == true)) {

            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " should not contain letters.");
            return false;

        }
        else
            return true;
    }
    public static boolean validateOnlyEmail(String controlName, String tbText, int minRanage, int maxRanage) {

        if (tbText == "" || tbText == null) {
            JavaWindowsFormUserInformers.showMsgWithJPane("Please enter " + controlName.toLowerCase() + ".");
            return false;
        } else if (tbText.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$") == false) {

            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is not correct.");
            return false;

        } else if (tbText.trim().length() < minRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too small.");
            return false;
        } else if (tbText.trim().length() > maxRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too long.");
            return false;
        } else
            return true;
    }

    public static boolean validateOnlyLength(String controlName, String tbText, int minRanage, int maxRanage) {
        if (tbText == "" || tbText == null) {
            JavaWindowsFormUserInformers.showMsgWithJPane("Please enter " + controlName.toLowerCase() + ".");
            return false;
        } else if (tbText.trim().length() < minRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too small.");
            return false;
        } else if (tbText.trim().length() > maxRanage) {
            JavaWindowsFormUserInformers.showMsgWithJPane(controlName + " is too long.");
            return false;
        } else
            return true;
    }

    public static boolean validateComboBox(String controlName, int selectedIndex) {

        if (selectedIndex<0) {
            JavaWindowsFormUserInformers.showMsgWithJPane("Please select a " + controlName.toLowerCase() + ".");
            return false;
        } else
            return true;
    }

    public static boolean compareTwoStrings(String str1, String str2, String errorMsg) {

        if (str1.equals(str2)==false) {
            JavaWindowsFormUserInformers.showMsgWithJPane(errorMsg);
            return false;
        } else
            return true;
    }

}

