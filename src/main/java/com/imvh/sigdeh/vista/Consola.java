package com.imvh.sigdeh.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Lectura de datos por consola: repite la pregunta hasta recibir un valor válido.
public class Consola {

    // Formato de fecha usado en toda la interfaz.
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner = new Scanner(System.in);

    // Lee un texto no vacío.
    public String leerTexto(String mensaje) {

        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  El dato no puede quedar vacío.");
        }

    }

    // Lee un número entero.
    public int leerEntero(String mensaje) {

        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Ingrese un número entero válido.");
            }
        }

    }

    // Lee un número decimal (acepta coma o punto como separador).
    public double leerDecimal(String mensaje) {

        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("  Ingrese un número válido (ej: 850000.50).");
            }
        }

    }

    // Lee una fecha en formato dd/MM/aaaa.
    public LocalDate leerFecha(String mensaje) {

        while (true) {
            System.out.print(mensaje + " (dd/mm/aaaa): ");
            try {
                return LocalDate.parse(scanner.nextLine().trim(), FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                System.out.println("  Fecha inválida. Use el formato dd/mm/aaaa (ej: 15/03/2019).");
            }
        }

    }

    // Lee una respuesta S/N.
    public boolean leerSiNo(String mensaje) {

        while (true) {
            System.out.print(mensaje + " (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();
            if (respuesta.equals("S")) {
                return true;
            }
            if (respuesta.equals("N")) {
                return false;
            }
            System.out.println("  Responda S o N.");
        }

    }

    // Cierra la entrada estándar.
    public void cerrar() {

        scanner.close();

    }

}
