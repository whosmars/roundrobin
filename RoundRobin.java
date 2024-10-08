import java.util.LinkedList;
import java.util.Queue;

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
    private static final int QUANTUM = 2;  // Quantum de 2 segundos

    public static void main(String[] args) {
        Queue<Proceso> colaListos = new LinkedList<>();
        Queue<Proceso> colaES = new LinkedList<>();

        // Crear procesos
        Proceso p1 = new Proceso("1", "P1", 100, 5, 1, 2, 0);
        Proceso p2 = new Proceso("2", "P2", 200, 3, 2, 1, 1);
        Proceso p3 = new Proceso("3", "P3", 150, 7, 3, 3, 2);

        // Añadir procesos a la cola de listos
        colaListos.add(p1);
        colaListos.add(p2);
        colaListos.add(p3);

        // Simular el Round Robin
        while (!colaListos.isEmpty()) {
            Proceso procesoActual = colaListos.poll();

            Thread hiloProceso = new Thread(procesoActual);
            hiloProceso.start();

            try {
                // Ejecutar el proceso por el tiempo de quantum o hasta que termine
                int tiempoEjecucion = Math.min(QUANTUM, procesoActual.tiempoEjecucion);
                Thread.sleep(tiempoEjecucion * 1000);

                // Verificar si el proceso terminó
                if (procesoActual.tiempoEjecucion > QUANTUM) {
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

