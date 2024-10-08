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
            while (tiempoEjecucion > 0) {
                // Simular un quantum de ejecución de 1 segundo
                Thread.sleep(1000); 
                tiempoEjecucion--; // Reducir el tiempo de ejecución
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
    private static int memoriaMaxima = 1024;  // Capacidad máxima de memoria en KB
    private static int memoriaDisponible = memoriaMaxima;  // Memoria disponible

    private static int quantum = 2;  // Quantum inicial de 2 segundos
    private static int contadorId = 1;  // Contador para IDs únicos de procesos

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
                    // Añadir procesos manualmente solo con quantum y ráfaga
                    System.out.print("Ingrese el quantum para los procesos: ");
                    quantum = scanner.nextInt();

                    Queue<Proceso> colaListosManual = new LinkedList<>();
                    boolean agregarOtro = true;

                    while (agregarOtro) {
                        String id = generarId();
                        String nombre = generarNombre();

                        System.out.print("Ingrese el tiempo de ejecución (ráfaga) del proceso: ");
                        int tiempoEjecucion = scanner.nextInt();





                        System.out.print("Ingrese el tamaño del proceso (KB): ");
                        int tamanoProceso = scanner.nextInt();

                        if (tamanoProceso <= memoriaDisponible) {
                            // Crear proceso si hay suficiente memoria disponible
                            Proceso procesoManual = new Proceso(id, nombre, tamanoProceso, tiempoEjecucion, generarPrioridad(), generarTiempoES(), generarTiempoLlegada());
                            colaListosManual.add(procesoManual);
                            memoriaDisponible -= tamanoProceso;  // Restar el tamaño del proceso de la memoria disponible
                            System.out.println("Subió el proceso " + nombre + " y restan " + memoriaDisponible + " unidades de memoria.");
                            System.out.print("¿Desea agregar otro proceso? (s/n): ");
                            char respuesta = scanner.next().charAt(0);
                            agregarOtro = respuesta == 's' || respuesta == 'S';
                        } else {
                            System.out.println("No hay suficiente memoria disponible para el proceso ");
                            agregarOtro = false;
                        }





                        // Crear proceso con los atributos manuales y el resto generados automáticamente
                        

                        
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
        Proceso p1 = new Proceso(generarId(), generarNombre(), generarTamano(), 5, generarPrioridad(), generarTiempoES(), generarTiempoLlegada());
        Proceso p2 = new Proceso(generarId(), generarNombre(), generarTamano(), 3, generarPrioridad(), generarTiempoES(), generarTiempoLlegada());
        Proceso p3 = new Proceso(generarId(), generarNombre(), generarTamano(), 7, generarPrioridad(), generarTiempoES(), generarTiempoLlegada());

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

            // Simulamos la ejecución por el tiempo de quantum
            try {
                int tiempoEjecutado = Math.min(quantum, procesoActual.tiempoEjecucion);
                Thread.sleep(tiempoEjecutado * 1000);  // Simulamos la ejecución por el tiempo calculado
                procesoActual.tiempoEjecucion -= tiempoEjecutado;  // Reducimos el tiempo de ejecución restante

                // Verificar si el proceso terminó
                if (procesoActual.tiempoEjecucion > 0) {
                    System.out.println("Proceso " + procesoActual.nombre + " no terminó, regresando a la cola con " + procesoActual.tiempoEjecucion + " segundos restantes.");
                    colaListos.offer(procesoActual);  // Reinsertar el proceso al final de la cola
                } else {
                    System.out.println("Proceso " + procesoActual.nombre + " completado.");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupción durante la ejecución del proceso.");
            }
        }

        System.out.println("Todos los procesos han sido completados.");
    }

    // Métodos para generar valores automáticos
    public static String generarId() {
        return String.valueOf(contadorId++);
    }

    public static String generarNombre() {
        return "P" + (contadorId - 1);
    }

    public static int generarTamano() {
        return (int) (Math.random() * 500 + 100); // Tamaño aleatorio entre 100 y 600
    }

    public static int generarPrioridad() {
        return (int) (Math.random() * 5 + 1); // Prioridad aleatoria entre 1 y 5
    }

    public static int generarTiempoES() {
        return (int) (Math.random() * 5); // Tiempo E/S aleatorio entre 0 y 5
    }

    public static int generarTiempoLlegada() {
        return (int) (Math.random() * 10); // Tiempo de llegada aleatorio entre 0 y 10
    }
}
