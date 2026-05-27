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