# Informe de entrega

## Contexto general

El sistema modela una cooperativa de taxis que recibe solicitudes de pasajeros, las mantiene en espera, asigna conductores disponibles y registra el historial del servicio.

## Requisitos funcionales cubiertos

- **Recepcion de solicitudes**: `VentanaPrincipal` captura nombre, direccion, telefono, tipo de servicio y distancia desde un formulario grafico.
- **Gestion de solicitudes en espera**: `GestorSolicitudes` usa una `Queue<Solicitud>` para manejar el orden de llegada.
- **Atencion de la solicitud**: `atenderSiguiente()` asigna el primer conductor disponible que soporte el tipo de servicio.
- **Calculo de tarifa**: `TarifaEstandarStrategy` calcula base de $5.000 COP mas costo por kilometro.
- **Cierre del servicio**: `finalizarSolicitud()` marca la solicitud como finalizada y libera el conductor.
- **Historial**: se consulta desde el menu y se persiste en `data/historial_solicitudes.csv`.

## Requisitos tecnicos cubiertos

- **UML**: `docs/UML.md` contiene diagrama de clases, secuencia y casos de uso.
- **POO**: hay clases de dominio, encapsulamiento, herencia, abstraccion y polimorfismo.
- **SOLID**: las responsabilidades estan separadas entre UI, gestor, repositorio, fabrica y estrategia de tarifa.
- **Patron de diseno principal**: Factory Method/Simple Factory en `ServicioTaxiFactory`.
- **Patron adicional**: Strategy en `TarifaStrategy`.
- **Excepciones personalizadas**: `DatosInvalidosException`, `SolicitudNoEncontradaException` y `ConductorNoDisponibleException`.
- **Persistencia**: `ArchivoSolicitudRepository` guarda y carga solicitudes desde CSV.
- **Interfaz de usuario**: ventana grafica con `JFrame`, formulario, botones y tablas.
- **Interfaz alternativa**: menu por consola disponible con `--console`.

## Archivos principales

- `src/com/cooperativa/taxi/Main.java`: punto de entrada.
- `src/com/cooperativa/taxi/app/GestorSolicitudes.java`: logica central del sistema.
- `src/com/cooperativa/taxi/model`: entidades del dominio.
- `src/com/cooperativa/taxi/factory/ServicioTaxiFactory.java`: patron Factory.
- `src/com/cooperativa/taxi/pricing/TarifaStrategy.java`: patron Strategy.
- `src/com/cooperativa/taxi/repository/ArchivoSolicitudRepository.java`: persistencia.
- `src/com/cooperativa/taxi/ui/Consola.java`: menu interactivo.
- `src/com/cooperativa/taxi/ui/swing/VentanaPrincipal.java`: interfaz grafica Swing/JFrame.

## Comandos de ejecucion

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
java -cp out com.cooperativa.taxi.Main
```
