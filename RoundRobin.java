//hola amor buenas noches, este codigo ya funciona maso menos, lo unico que hace es lit seguir las instrucciones
//los problemas principales es que la funcion principal run() y rr() funcionan con dos sleep, haciendo que no sean independientes y les falte estructura
//otro problema es que solo se usa una list en vez de 2, por lo que hay cosas que pide el profe que no estan bien implementadas
//la estructura de proceso me parece correcta asi como el menu si no quieres hacer el javax


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
    int tiempoInicio; // Nuevo: para calcular tiempo de respuesta
    int tiempoFinalizacion; // Nuevo: para calcular tiempo de ejecución
    int tiempoEspera; // Nuevo: para calcular tiempo de espera total



    public Proceso(String id, String nombre, int tamano, int tiempoEjecucion, int prioridad, int tiempoES, int tiempoLlegada) {
        this.id = id;
        this.nombre = nombre;
        this.tamano = tamano;
        this.tiempoEjecucion = tiempoEjecucion;
        this.prioridad = prioridad;
        this.tiempoES = tiempoES;
        this.tiempoLlegada = tiempoLlegada;
        this.tiempoInicio = -1;  // Inicialmente no ha empezado
        this.tiempoEspera = 0;   // El tiempo de espera empieza en 0
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
        int tiempoActual = 0; // Reloj global para la simulación
        int totalEspera = 0;
        int totalEjecucion = 0;
        int totalRespuesta = 0;
        int numProcesos = colaListos.size();
    
        while (!colaListos.isEmpty()) {
            Proceso procesoActual = colaListos.poll();
    
            // Si es la primera vez que se ejecuta, registrar el tiempo de respuesta
            if (procesoActual.tiempoInicio == -1) {
                procesoActual.tiempoInicio = tiempoActual;  // Tiempo en que empezó a ejecutarse
                int tiempoRespuesta = procesoActual.tiempoInicio - procesoActual.tiempoLlegada;
                totalRespuesta += tiempoRespuesta;
            }
    
            // Simulamos la ejecución por el tiempo de quantum
            try {
                int tiempoEjecutado = Math.min(quantum, procesoActual.tiempoEjecucion);
                Thread.sleep(tiempoEjecutado * 1000);  // Simulamos la ejecución por el tiempo calculado
                procesoActual.tiempoEjecucion -= tiempoEjecutado;  // Reducimos el tiempo de ejecución restante
                tiempoActual += tiempoEjecutado;  // Avanzar el tiempo global de la simulación
    
                // Verificar si el proceso terminó
                if (procesoActual.tiempoEjecucion > 0) {
                    // El proceso no terminó, acumular tiempo de espera y devolverlo a la cola
                    procesoActual.tiempoEspera += (tiempoActual - procesoActual.tiempoInicio); // El tiempo que estuvo esperando
                    System.out.println("Proceso " + procesoActual.nombre + " no terminó, regresando a la cola con " + procesoActual.tiempoEjecucion + " segundos restantes.");
                    colaListos.offer(procesoActual);  // Reinsertar el proceso al final de la cola
                } else {
                    // El proceso terminó
                    procesoActual.tiempoFinalizacion = tiempoActual;  // Registrar tiempo de finalización
                    int tiempoEjecucion = procesoActual.tiempoFinalizacion - procesoActual.tiempoLlegada;
                    totalEjecucion += tiempoEjecucion;
    
                    System.out.println("Proceso " + procesoActual.nombre + " completado. Tiempo de ejecución total: " + tiempoEjecucion + " segundos. Liberando " + procesoActual.tamano + " unidades de memoria.");
                    memoriaDisponible += procesoActual.tamano;  // Liberar la memoria ocupada por el proceso
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupción durante la ejecución del proceso.");
            }
        }
    
        System.out.println("Todos los procesos han sido completados.");
    
        // Calcular y mostrar los promedios
        double promedioEspera = (double) totalEspera / numProcesos;
        double promedioEjecucion = (double) totalEjecucion / numProcesos;
        double promedioRespuesta = (double) totalRespuesta / numProcesos;
    
        System.out.println("\n--- Promedios ---");
        System.out.println("Tiempo promedio de espera: " + promedioEspera + " segundos.");
        System.out.println("Tiempo promedio de ejecución: " + promedioEjecucion + " segundos.");
        System.out.println("Tiempo promedio de respuesta: " + promedioRespuesta + " segundos.");
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
