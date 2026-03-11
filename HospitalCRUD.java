import java.io.*;
import java.util.Scanner;
class Patient implements Serializable 
{
    int id;
    String name;
    int age;
    String disease;
    String doctorAssigned;
    double billAmount;
    String status; 

    Patient(int id, String name, int age, String disease,
            String doctorAssigned, double billAmount, String status) 
            {
        this.id            = id;
        this.name          = name;
        this.age           = age;
        this.disease       = disease;
        this.doctorAssigned = doctorAssigned;
        this.billAmount    = billAmount;
        this.status        = status;
    }
}

class AppendableObjectOutputStream extends ObjectOutputStream {
    AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
    @Override
    protected void writeStreamHeader() throws IOException {
        // Suppressed to allow safe appending to existing binary file
    }
}

public class HospitalCRUD {

    static final String FILE_NAME = "hospital.dat";
    static Scanner sc = new Scanner(System.in);

    static void addPatient() {
        try {
            System.out.print("Enter Patient ID   : ");
            int id = sc.nextInt();
            sc.nextLine();

            if (idExists(id)) {
                System.out.println(" Patient ID already exists. Use a unique ID.");
                return;
            }

            System.out.print("Enter Name         : ");
            String name = sc.nextLine();

            System.out.print("Enter Age          : ");
            int age = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter Disease      : ");
            String disease = sc.nextLine();

            System.out.print("Enter Doctor Name  : ");
            String doctor = sc.nextLine();

            System.out.print("Enter Bill Amount  : ");
            double bill = sc.nextDouble();
            sc.nextLine();

            System.out.print("Enter Status (Admitted/Discharged): ");
            String status = sc.nextLine();

            FileOutputStream fos = new FileOutputStream(FILE_NAME, true);
            ObjectOutputStream oos;

            if (new File(FILE_NAME).length() == 0)
                oos = new ObjectOutputStream(fos);
            else
                oos = new AppendableObjectOutputStream(fos);

            oos.writeObject(new Patient(id, name, age, disease, doctor, bill, status));
            oos.close();

            System.out.println("✅ Patient record added successfully.");

        } 
        catch (Exception e) 
        {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    static void viewPatients() 
    {
        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_NAME));

           
            System.out.printf("║ %-4s %-15s %-4s %-15s %-15s %10s %-12s ║%n","ID", "Name", "Age", "Disease", "Doctor", "Bill", "Status");
     
            boolean found = false;
            while (true) {
                Patient p = (Patient) ois.readObject();
                System.out.printf("║ %-4d %-15s %-4d %-15s %-15s %10.2f %-12s ║%n",
                    p.id, p.name, p.age, p.disease,
                    p.doctorAssigned, p.billAmount, p.status);
                found = true;
            }

        } catch (EOFException e) {
            System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
        } catch (FileNotFoundException e) {
            System.out.println("  No records found. Add a patient first.");
        } catch (Exception e) {
            System.out.println("Error reading records: " + e.getMessage());
        }
    }

    static void searchPatient() {
        System.out.print("Enter Patient ID to search: ");
        int id = sc.nextInt();

        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_NAME));

            boolean found = false;
            while (true) {
                Patient p = (Patient) ois.readObject();
                if (p.id == id) {
                    System.out.println("\n--- Patient Details ---");
                    System.out.println("ID       : " + p.id);
                    System.out.println("Name     : " + p.name);
                    System.out.println("Age      : " + p.age);
                    System.out.println("Disease  : " + p.disease);
                    System.out.println("Doctor   : " + p.doctorAssigned);
                    System.out.println("Bill     : ₹" + p.billAmount);
                    System.out.println("Status   : " + p.status);
                    found = true;
                    break;
                }
            }
            ois.close();
            if (!found) System.out.println(" Patient ID " + id + " not found.");

        } catch (EOFException e) {
            System.out.println("❌ Patient ID " + id + " not found.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void updatePatient() {
        System.out.print("Enter Patient ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        boolean found = false;

        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_NAME));
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream("temp.dat"));

            while (true) {
                Patient p = (Patient) ois.readObject();

                if (p.id == id) {
                    found = true;
                    System.out.println("What do you want to update?");
                    System.out.println("1. Disease");
                    System.out.println("2. Doctor Assigned");
                    System.out.println("3. Bill Amount");
                    System.out.println("4. Status");
                    System.out.println("5. All fields");
                    System.out.print("Choice: ");
                    int ch = sc.nextInt();
                    sc.nextLine();

                    switch (ch) {
                        case 1 -> {
                            System.out.print("New Disease: ");
                            p.disease = sc.nextLine();
                        }
                        case 2 -> {
                            System.out.print("New Doctor: ");
                            p.doctorAssigned = sc.nextLine();
                        }
                        case 3 -> {
                            System.out.print("New Bill Amount: ");
                            p.billAmount = sc.nextDouble();
                        }
                        case 4 -> {
                            System.out.print("New Status (Admitted/Discharged): ");
                            p.status = sc.nextLine();
                        }
                        case 5 -> {
                            System.out.print("New Disease      : "); p.disease = sc.nextLine();
                            System.out.print("New Doctor       : "); p.doctorAssigned = sc.nextLine();
                            System.out.print("New Bill Amount  : "); p.billAmount = sc.nextDouble(); sc.nextLine();
                            System.out.print("New Status       : "); p.status = sc.nextLine();
                        }
                        default -> System.out.println("Invalid choice, no changes made.");
                    }
                }
                oos.writeObject(p); 
            }

        } catch (EOFException e) {
            // Done reading
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        new File(FILE_NAME).delete();
        new File("temp.dat").renameTo(new File(FILE_NAME));

        if (found) System.out.println("✅ Patient record updated.");
        else        System.out.println("❌ Patient ID " + id + " not found.");
    }

    static void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        int id = sc.nextInt();

        boolean found = false;

        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_NAME));
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream("temp.dat"));

            while (true) {
                Patient p = (Patient) ois.readObject();
                if (p.id != id) {
                    oos.writeObject(p);  // Keep records that don't match
                } else {
                    found = true;        // Skip (delete) the matched record
                }
            }

        } catch (EOFException e) {
            // Done reading
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Replace original file with updated temp file
        new File(FILE_NAME).delete();
        new File("temp.dat").renameTo(new File(FILE_NAME));

        if (found) System.out.println("✅ Patient record deleted.");
        else        System.out.println("❌ Patient ID " + id + " not found.");
    }

    static boolean idExists(int id) {
        try {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(FILE_NAME));
            while (true) {
                Patient p = (Patient) ois.readObject();
                if (p.id == id) { ois.close(); return true; }
            }
        } catch (Exception e) {
            return false; 
        }
    }
    public static void main(String[] args) {
        int choice;

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   🏥  HOSPITAL MANAGEMENT SYSTEM     ║");
        System.out.println("╚══════════════════════════════════════╝");

        do {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Add Patient      (CREATE)");
            System.out.println("2. View All Patients(READ)");
            System.out.println("3. Search Patient   (READ by ID)");
            System.out.println("4. Update Patient   (UPDATE)");
            System.out.println("5. Delete Patient   (DELETE)");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> addPatient();
                case 2 -> viewPatients();
                case 3 -> searchPatient();
                case 4 -> updatePatient();
                case 5 -> deletePatient();
                case 6 -> System.out.println("👋 Thank you for using HMS. Goodbye!");
                default -> System.out.println("❌ Invalid choice. Try again.");
            }

        } while (choice != 6);
    }
}
