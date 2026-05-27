## Sección UML

Diagrama de clases (PlantUML):

```plantuml
@startuml
skinparam classAttributeIconSize 0

abstract class ServicioTaxi {
    - tipo: TipoServicio
    + {abstract} factorTarifa(): double
    + {abstract} descripcion(): String
}

class ServicioEstandar {
    + factorTarifa(): double
    + descripcion(): String
}

class ServicioCarga {
    + factorTarifa(): double
    + descripcion(): String
}

class ServicioMascotas {
    + factorTarifa(): double
    + descripcion(): String
}

class Solicitud {
    - id: int
    - distanciaKm: double
    - costo: double
    - estado: EstadoSolicitud
    + finalizar(): void
    + asignar(con: Conductor, costo: double): void
}

class Conductor {
    - nombre: String
    - disponible: boolean
    + asignar(): void
}

class Vehiculo {
    - placa: String
    - modelo: String
}

class GestorSolicitudes {
    - colaEspera: Queue<Solicitud>
    - historial: List<Solicitud>
    - conductores: List<Conductor>
    + registrarSolicitud(n: String, d: String, t: String, tipo: TipoServicio, dist: double): Solicitud
    + atenderSiguiente(): Solicitud
    + finalizarSolicitud(id: int): void
}

class ServicioTaxiFactory {
    + crear(tipo: TipoServicio): ServicioTaxi
}

class RutaBloqueadaException
class ConductorNoDisponibleException
class DatosInvalidosException

enum TipoServicio {
    ESTANDAR
    CARGA
    MASCOTAS
}

ServicioTaxi <|-- ServicioEstandar
ServicioTaxi <|-- ServicioCarga
ServicioTaxi <|-- ServicioMascotas

GestorSolicitudes --> ServicioTaxiFactory
GestorSolicitudes o-- Conductor
GestorSolicitudes o-- Solicitud

Solicitud o-- ServicioTaxi
Solicitud o-- Conductor

Conductor *-- Vehiculo
Vehiculo --> TipoServicio

GestorSolicitudes ..> RutaBloqueadaException
GestorSolicitudes ..> ConductorNoDisponibleException
GestorSolicitudes ..> DatosInvalidosException

@enduml
```

Descripción de elementos:

- ServicioTaxi (abstracto): define la interfaz de servicios con factorTarifa() y descripcion().
- ServicioEstandar / ServicioCarga / ServicioMascotas: implementaciones concretas que suministran el factor de tarifa y la descripción.
- Solicitud: representa una petición de servicio; contiene id, distancia, costo y estado; puede asignarse y finalizarse.
- Conductor: entidad con nombre, disponibilidad y método para asignarse a una solicitud.
- Vehiculo: datos básicos del vehículo asociado al conductor.
- GestorSolicitudes: coordina cola de espera, historial y conductores; registra, atiende y finaliza solicitudes.
- ServicioTaxiFactory: fabrica de servicios según TipoServicio.
- Excepciones: RutaBloqueadaException, ConductorNoDisponibleException, DatosInvalidosException para errores del flujo.
- TipoServicio (enum): ESTANDAR, CARGA, MASCOTAS.

Uso sugerido:
- Incluir este bloque PlantUML en la documentación para generar el diagrama.
- Complementar con diagramas de secuencia o de estados según sea necesario.
