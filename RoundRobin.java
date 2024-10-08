import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Proceso implements Runnable {
    String id;
    String nombre;
    int tamano;
    int tiempoEjecucion;
    int prioridad;
    int tiempoES;
    int tiempoLlegada;

    public Proceso(String id, String nombre, int tamano, int tiempoEjecucion, int prioridad, int tiempoES, int tiempoLlegada) {
        this.id = id;
        this.nombre = nombre;
        this.tamano = tamano;
        this.tiempoEjecucion = tiempoEjecucion;
        this.prioridad = prioridad;
        this.tiempoES = tiempoES;
        this.tiempoLlegada = tiempoLlegada;
    }

    @Override
    public void run() {
        try {
            System.out.println("Proceso " + nombre + " en ejecución...");
            while (tiempoEjecucion > 0) {
                Thread.sleep(1000); // Simulamos un segundo de ejecución
                tiempoEjecucion--;  // Reducimos el tiempo de ejecución
                System.out.println("Proceso " + nombre + " ejecutándose. Tiempo restante: " + tiempoEjecucion);
            }
            System.out.println("Proceso " + nombre + " completado.");
        } catch (InterruptedException e) {
            System.out.println("Proceso " + nombre + " interrumpido.");
        }
    }

    public String toString() {
        return "Proceso{id='" + id + "', nombre='" + nombre + "', tamaño=" + tamano + ", tiempoEjecucion=" + tiempoEjecucion + ", prioridad=" + prioridad + ", tiempoES=" + tiempoES + ", tiempoLlegada=" + tiempoLlegada + "}";
    }
}

public class RoundRobin {
    private static int quantum = 2;  // Quantum inicial de 2 segundos

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n----- Menú Round Robin -----");
            System.out.println("1. Correr simulación automática");
            System.out.println("2. Añadir procesos manualmente");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    // Simulación automática con procesos predefinidos
                    correrSimulacionAutomatica();
                    break;

                case 2:
                    // Añadir procesos manualmente
                    System.out.print("Ingrese el quantum para los procesos: ");
                    quantum = scanner.nextInt();

                    Queue<Proceso> colaListosManual = new LinkedList<>();
                    boolean agregarOtro = true;

                    while (agregarOtro) {
                        System.out.print("Ingrese el ID del proceso: ");
                        String id = scanner.next();

                        System.out.print("Ingrese el nombre del proceso: ");
                        String nombre = scanner.next();

                        System.out.print("Ingrese el tiempo de ejecución (ráfaga) del proceso: ");
                        int tiempoEjecucion = scanner.nextInt();

                        // Creamos un proceso con los atributos dados (el resto se pone a 0 o valores arbitrarios)
                        Proceso procesoManual = new Proceso(id, nombre, 0, tiempoEjecucion, 0, 0, 0);
                        colaListosManual.add(procesoManual);

                        System.out.print("¿Desea agregar otro proceso? (s/n): ");
                        char respuesta = scanner.next().charAt(0);
                        agregarOtro = respuesta == 's' || respuesta == 'S';
                    }

                    correrSimulacionConCola(colaListosManual);
                    break;

                case 3:
                    salir = true;
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
                    break;
            }
        }

        scanner.close();
    }

    public static void correrSimulacionAutomatica() {
        Queue<Proceso> colaListos = new LinkedList<>();

        // Crear procesos predefinidos
        Proceso p1 = new Proceso("1", "P1", 100, 5, 1, 2, 0);
        Proceso p2 = new Proceso("2", "P2", 200, 3, 2, 1, 1);
        Proceso p3 = new Proceso("3", "P3", 150, 7, 3, 3, 2);

        // Añadir procesos a la cola de listos
        colaListos.add(p1);
        colaListos.add(p2);
        colaListos.add(p3);

        // Ejecutar la simulación con los procesos predefinidos
        correrSimulacionConCola(colaListos);
    }

    public static void correrSimulacionConCola(Queue<Proceso> colaListos) {
        // Simular el Round Robin con los procesos en la cola de listos
        while (!colaListos.isEmpty()) {
            Proceso procesoActual = colaListos.poll();

            Thread hiloProceso = new Thread(procesoActual);
            hiloProceso.start();

            try {
                // Ejecutar el proceso por el tiempo de quantum o hasta que termine
                int tiempoEjecucion = Math.min(quantum, procesoActual.tiempoEjecucion);
                Thread.sleep(tiempoEjecucion * 1000);

                // Verificar si el proceso terminó
                if (procesoActual.tiempoEjecucion > quantum) {
                    System.out.println("Proceso " + procesoActual.nombre + " no terminó, regresando a la cola.");
                    colaListos.add(procesoActual);
                } else {
                    System.out.println("Proceso " + procesoActual.nombre + " finalizó.");
                }

                hiloProceso.join(); // Esperar a que termine el hilo
            } catch (InterruptedException e) {
                System.out.println("Interrupción durante la ejecución del proceso.");
            }
        }

        System.out.println("Todos los procesos han sido completados.");
    }
}
