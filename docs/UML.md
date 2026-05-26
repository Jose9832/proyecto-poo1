# Diseno UML

## Diagrama de clases

```mermaid
classDiagram
    class Main
    class Consola
    class GestorSolicitudes {
        -Queue~Solicitud~ colaEspera
        -List~Solicitud~ historial
        -List~Conductor~ conductores
        +registrarSolicitud()
        +atenderSiguiente()
        +finalizarSolicitud()
        +cancelarSolicitud()
    }

    class Solicitud {
        -int id
        -Pasajero pasajero
        -ServicioTaxi servicio
        -EstadoSolicitud estado
        -Conductor conductor
        -double costo
        +asignar()
        +finalizar()
        +cancelar()
    }

    class Pasajero
    class Conductor
    class Vehiculo
    class ServicioTaxi {
        <<abstract>>
        +factorTarifa() double
        +descripcion() String
    }
    class ServicioEstandar
    class ServicioCompartido
    class ServicioCarga
    class ServicioTaxiFactory {
        +crear(TipoServicio) ServicioTaxi
    }
    class TarifaStrategy {
        <<interface>>
        +calcular(ServicioTaxi, double) double
    }
    class TarifaEstandarStrategy
    class SolicitudRepository {
        <<interface>>
        +guardar(List~Solicitud~)
    }
    class ArchivoSolicitudRepository

    Main --> GestorSolicitudes
    Main --> Consola
    Consola --> GestorSolicitudes
    GestorSolicitudes --> Solicitud
    GestorSolicitudes --> Conductor
    GestorSolicitudes --> ServicioTaxiFactory
    GestorSolicitudes --> TarifaStrategy
    GestorSolicitudes --> SolicitudRepository
    Solicitud --> Pasajero
    Solicitud --> ServicioTaxi
    Solicitud --> Conductor
    Conductor --> Vehiculo
    ServicioTaxi <|-- ServicioEstandar
    ServicioTaxi <|-- ServicioCompartido
    ServicioTaxi <|-- ServicioCarga
    TarifaStrategy <|.. TarifaEstandarStrategy
    SolicitudRepository <|.. ArchivoSolicitudRepository
```

## Diagrama de secuencia: atencion de solicitud

```mermaid
sequenceDiagram
    actor Usuario
    participant Consola
    participant Gestor as GestorSolicitudes
    participant Factory as ServicioTaxiFactory
    participant Tarifa as TarifaStrategy
    participant Repo as SolicitudRepository

    Usuario->>Consola: registra datos de solicitud
    Consola->>Gestor: registrarSolicitud(...)
    Gestor->>Factory: crear(tipoServicio)
    Factory-->>Gestor: ServicioTaxi
    Gestor->>Repo: guardar(historial)
    Usuario->>Consola: atender siguiente
    Consola->>Gestor: atenderSiguiente()
    Gestor->>Tarifa: calcular(servicio, distancia)
    Tarifa-->>Gestor: costo
    Gestor->>Repo: guardar(historial)
    Gestor-->>Consola: solicitud asignada
```

## Casos de uso principales

- Registrar solicitud.
- Consultar solicitudes en espera.
- Atender solicitud y asignar conductor.
- Cancelar solicitud en espera.
- Finalizar servicio.
- Consultar historial.
