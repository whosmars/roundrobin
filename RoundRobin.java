import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Proceso implements Runnable {
    String id;
    String nombre;
    int tamano;
    int tiempoEjecucion;
    int tiempoRestante;
    int prioridad;
    int tiempoLlegada;

    int tiempoInicio = -1;  // Inicialmente, tiempoInicio es -1 para indicar que no ha iniciado
    int tiempoFinalizacion = 0;

    public Proceso(String id, String nombre, int tamano, int tiempoEjecucion, int prioridad, int tiempoLlegada) {
        this.id = id;
        this.nombre = nombre;
        this.tamano = tamano;
        this.tiempoEjecucion = tiempoEjecucion;
        this.tiempoRestante = tiempoEjecucion;
        this.prioridad = prioridad;
        this.tiempoLlegada = tiempoLlegada;
    }

    @Override
    public void run() {}

    @Override
    public String toString() {
        return String.format("| %-5s | %-7s | %-7d | %-12d | %-10d | %-12d |", id, nombre, tamano, tiempoEjecucion, tiempoRestante, tiempoLlegada);
    }
}

public class RoundRobin {
    private static final int MEMORIA_MAXIMA = 1024;
    private static int memoriaDisponible = MEMORIA_MAXIMA;
    private static int quantum = 2; 
    private static int contadorId = 1; 
    private static int tiempoGlobal = 0;

    private static Queue<Proceso> colaListos = new LinkedList<>();
    private static Queue<Proceso> colaListosEjecucion = new LinkedList<>();
    private static Queue<Proceso> colaCompletados = new LinkedList<>();

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
                    resetearSimulacion();
                    System.out.println("Quantum seleccionado: " + quantum + " ms");
                    correrSimulacionAutomatica();
                    break;
                case 2:
                    resetearSimulacion();
                    System.out.print("Ingrese el quantum para los procesos: ");
                    quantum = scanner.nextInt();
                    System.out.println("Quantum seleccionado: " + quantum + " ms");
                    agregarProcesosManualmente();
                    ajustarTiempos();
                    mostrarProcesosIniciales();
                    roundRobin();
                    break;
                case 3:
                    salir = true;
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
    }

    private static void resetearSimulacion() {
        colaListos.clear();
        colaListosEjecucion.clear();
        colaCompletados.clear();
        memoriaDisponible = MEMORIA_MAXIMA;
        tiempoGlobal = 0;
        contadorId = 1;
        System.out.println("Simulación reiniciada. Memoria y colas de procesos vaciadas.");
    }

    private static void correrSimulacionAutomatica() {
        colaListos.add(new Proceso(generarId(), "Proceso_A", 200, 5, 1, 0)); 
        colaListos.add(new Proceso(generarId(), "Proceso_B", 300, 4, 1, 1)); 
        colaListos.add(new Proceso(generarId(), "Proceso_C", 150, 6, 1, 2)); 
        colaListos.add(new Proceso(generarId(), "Proceso_D", 500, 3, 1, 3)); 
        colaListos.add(new Proceso(generarId(), "Proceso_E", 100, 8, 1, 4)); 

        System.out.println("Simulación automática creada con procesos de prueba.");
        mostrarProcesosIniciales();
        roundRobin();
    }

    private static void agregarProcesosManualmente() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String id = generarId();
            System.out.print("Ingrese el nombre del proceso: ");
            String nombre = scanner.next();
            System.out.print("Ingrese el tamaño del proceso (KB): ");
            int tamano = scanner.nextInt();
            System.out.print("Ingrese el tiempo de ejecución del proceso: ");
            int tiempoEjecucion = scanner.nextInt();
            System.out.print("Ingrese el tiempo de llegada del proceso: ");
            int tiempoLlegada = scanner.nextInt();

            Proceso proceso = new Proceso(id, nombre, tamano, tiempoEjecucion, 1, tiempoLlegada);
            colaListos.add(proceso);
            System.out.print("¿Desea agregar otro proceso? (s/n): ");
            String respuesta = scanner.next();
            if (respuesta.equalsIgnoreCase("n")) {
                break;
            }
        }
    }

    private static void ajustarTiempos() {
        if (!colaListos.isEmpty()) {
            int tiempoInicial = colaListos.peek().tiempoLlegada;
            for (Proceso p : colaListos) {
                p.tiempoLlegada -= tiempoInicial;
            }
            tiempoGlobal = 0;
        }
    }

    private static void mostrarProcesosIniciales() {
        System.out.println("\n******** Procesos Iniciales ********");
        imprimirCola(colaListos, "Procesos a Ejecutar");
    }

    private static void roundRobin() {
        cargarProcesosEnMemoria();
        while (!colaListos.isEmpty() || !colaListosEjecucion.isEmpty()) {
            Proceso procesoActual = colaListosEjecucion.poll();

            if (procesoActual != null) {
                if (procesoActual.tiempoInicio == -1) {  // Establecer el tiempo de inicio solo la primera vez
                    procesoActual.tiempoInicio = tiempoGlobal;
                }

                System.out.println("\n--- Proceso en ejecución ---");
                int tiempoEjecucionActual = Math.min(quantum, procesoActual.tiempoRestante);
                
                for (int i = tiempoEjecucionActual; i > 0; i--) {
                    System.out.printf("%s en ejecución %d msg\n", procesoActual.id, procesoActual.tiempoRestante);
                    procesoActual.tiempoRestante--;
                    tiempoGlobal++;
                }

                if (procesoActual.tiempoRestante > 0) {
                    colaListos.add(procesoActual); 
                    memoriaDisponible += procesoActual.tamano; 
                    imprimirColas("Proceso " + procesoActual.id + " reinsertado en la cola de listos.");
                } else {
                    procesoActual.tiempoFinalizacion = tiempoGlobal;
                    colaCompletados.add(procesoActual);
                    memoriaDisponible += procesoActual.tamano;
                    imprimirColas("Proceso " + procesoActual.id + " completado.");
                }
                cargarProcesosEnMemoria();
            }
        }

        System.out.println("\n***** Todos los procesos han sido completados *****\n");
        mostrarTiempos();
    }

    private static void cargarProcesosEnMemoria() {
        while (!colaListos.isEmpty()) {
            Proceso proceso = colaListos.peek(); 
            if (proceso.tamano <= memoriaDisponible) {
                colaListosEjecucion.add(proceso);
                memoriaDisponible -= proceso.tamano;
                colaListos.poll(); 
                imprimirColas("Proceso subido a memoria: " + proceso.id + ". Memoria disponible: " + memoriaDisponible + " KB.");
            } else {
                break; 
            }
        }
    }

    private static String generarId() {
        return "P" + (contadorId++);
    }

    private static void imprimirColas(String mensaje) {
        System.out.println("\n" + mensaje);
        System.out.println("\n--- Cola de Procesos Listos (Mediano Plazo) ---");
        imprimirCola(colaListos, "Cola de procesos listos (espera para subir a memoria)");

        System.out.println("\n--- Cola de Procesos Listos para Ejecución ---");
        imprimirCola(colaListosEjecucion, "Cola de procesos listos para ejecución");
    }

    private static void imprimirCola(Queue<Proceso> cola, String nombreCola) {
        System.out.println("\n" + nombreCola + ":");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-7s | %-7s | %-12s | %-10s | %-12s |\n", 
                          "ID", "Nombre", "Tamaño", "Tiempo Total", "Restante", "Tiempo Llegada");
        System.out.println("------------------------------------------------------------------------------------------");

        for (Proceso p : cola) {
            System.out.printf("| %-5s | %-7s | %-7d | %-12d | %-10d | %-12d |\n",
                              p.id, p.nombre, p.tamano, p.tiempoEjecucion, p.tiempoRestante, p.tiempoLlegada);
        }

        System.out.println("------------------------------------------------------------------------------------------");
    }

    private static void mostrarTiempos() {
        System.out.println("\n********* Tiempos Finales de los Procesos *********");
        System.out.printf("| %-5s | %-12s | %-15s | %-10s | %-10s |\n", "ID", "Tiempo Espera", "Tiempo Ejecución", "Tiempo Respuesta", "Tiempo Llegada");
        int totalEspera = 0, totalEjecucion = 0, totalRespuesta = 0;

        for (Proceso p : colaCompletados) {
            int tiempoEspera = p.tiempoInicio - p.tiempoLlegada;
            int tiempoEjecucion = p.tiempoFinalizacion - p.tiempoLlegada;
            int tiempoRespuesta = p.tiempoInicio - p.tiempoLlegada;

            totalEspera += tiempoEspera;
            totalEjecucion += tiempoEjecucion;
            totalRespuesta += tiempoRespuesta;

            System.out.printf("| %-5s | %-12d | %-15d | %-10d | %-10d |\n", p.id, tiempoEspera, tiempoEjecucion, tiempoRespuesta, p.tiempoLlegada);
        }

        int n = colaCompletados.size();
        System.out.printf("\nTiempos Promedio: Espera = %.2f, Ejecución = %.2f, Respuesta = %.2f\n",
                          (double) totalEspera / n, (double) totalEjecucion / n, (double) totalRespuesta / n);
    }
}
