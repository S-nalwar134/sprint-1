package com.InterviewSimulator;

import com.InterviewSimulator.service.InterviewService;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InterviewService interviewService = new InterviewService();

        while (true) {
            System.out.println("\n==== AI Interview Simulator ====");
            System.out.println("1. Create Account");
            System.out.println("2. Schedule Interview Session");
            System.out.println("3. Start Instant Interview");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidIntegerInput(scanner);

            switch (choice) {
                case 1:
                    interviewService.createAccount(scanner);
                    break;
                case 2:
                    interviewService.scheduleInterviewSession(scanner);
                    break;
                case 3:
                	 System.out.print("Enter your Email: ");
                     String email = scanner.nextLine();
                     interviewService.startInstantInterview(email, scanner);                   
                     break;
                case 4:
                    System.out.println("Exiting... Thank you for using AI Interview Simulator.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
    

    private static int getValidIntegerInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input! Please enter a number: ");
            }scanner.close(); // ✅ Close scanner after usage
        }
    }
    
}
