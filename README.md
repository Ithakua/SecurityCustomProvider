# Custom Security-Provider Kafka

Este repositorio es parte de un trabajo final de grado y ofrece un proyecto Java capaz de cargar proveedores de seguridad con soporte de criptografía post-cuántica para [Apache Kafka](https://kafka.apache.org/) utilizando la librería [Bouncy Castle](https://www.bouncycastle.org/). También incluye las herramientas para poder medir el tiempo de handshake una vez la aplicación está desplegada.

## ¿Qué contiene el proyecto?

La estructura del proyecto es la siguiente:

- **normalProv**: Este paquete incluye la clase que permite añadir el proveedor _Bouncy Castle_ en Kafka.
- **pqcProv**: Paquete que incluye la clase que permite añadir el proveedor _Bouncy Castle PQ_ en Kafka.
- **sslProv**: Este paquete incluye la clase que permite añadir el proveedor _Bouncy Castle JSSE_ en Kafka.
- **config**: Incluye la clase `NamedGroupsConfig` que permite cargar los grupos criptográficos disponibles del proveedor BouncyCastleJSSEProvider.
- **testHandshake**: Paquete que incluye las herramientas necesarias para medir el tiempo de handshake con el servidor de N peticiones, tanto con autenticación de cliente como sin autorización de cliente.

### Consideraciones importantes

_El gestor de dependencias del proyecto es Maven, y la versión de BouncyCastle con la que se ha trabajado en este proyecto es la `1.78.1`. En caso de que se quiera cambiar de versión, solo habría que modificar el `pom.xml` y cambiarla por la que se necesite._

## Cómo generar los JAR

El principal propósito de este proyecto es generar los archivos .jar que permitan a la infraestructura del repositorio [post-quantum-support-kafka](https://github.com/Ithakua/post-quantum-support-kafka) implementar los proveedores de seguridad necesarios para poder utilizar los grupos criptográficos que permiten las comunicaciones ML-KEM. Para ello, se tiene que generar un JAR del proyecto que incluya las dependencias necesarias (uber-jar).

Con este JAR, si se guarda en el directorio `./KafkaApp/libs` del repositorio `post-quantum-support-kafka`, permite desplegar una infraestructura Kafka con un proveedor personalizado de seguridad y un proveedor personalizado JSSE:

- **CustomSecurityProvider_mlkem.jar**: Nombre al que apuntan las variables de entorno Docker para desplegar el servidor de Kafka con una configuración ML-KEM.
- **CustomSecurityProvider_allgroups.jar**: Nombre de referencia para una configuración KEM mixta que incluya tanto grupos KEM clásicos como KEM post-cuánticos.

### Consideraciones importantes

_Los grupos que se van a cargar en la configuración de Kafka se encuentran en la clase `NamedGroupsConfig`. En caso de que se quieran utilizar otros adicionales, se debe indicar desde esa clase._

_Al trabajar en este proyecto y hacer pruebas, es normal que algunos .jar generados den problema a la hora del despliegue. Si se detecta un internal_error(80) alert o da problemas en alguno de los handshakes cuando se ejecuta el test, se recomienda regenerar el JAR y volver a cargarlo. Si el error persiste, se recomienda recargar las dependencias de Maven._

## Cómo medir los tiempos de handshake

Para poder hacer uso de esta funcionalidad, se necesita tener descargada y desplegada la infraestructura Kafka del repositorio `post-quantum-support-kafka`.

Una vez desplegado el servidor de Kafka y en función de si el parámetro `KAFKA_SSL_CLIENT_AUTH: 'required'` del docker-compose.yaml está activo, se puede hacer uso de dos herramientas:

- **TestHandshake**: Esta herramienta permite probar los algoritmos indicados en la clase `NamedGroupsConfig`, generando N peticiones (parametro a seleccionar) al servidor y midiendo el tiempo de cada grupo.
- **TestHandshake_clientAuth**: Es una modificación de la herramienta anterior y permite generar N peticiones al servidor con autenticación por parte de cliente.

Una vez finalizados todos los acuerdos Handshake, se podrá ver el `.csv` generado en la carpeta `./testing` del repositorio `./post-quantum-support-kafka`, que se guardará con el nombre `handshake_times.csv`.

### Consideraciones importantes

_Es importante tener en cuenta que estas clases miden el tiempo de handshake utilizando la librería de BouncyCastle. Lo más recomendable para evitar problemas es realizar las conexiones SSL con un servidor que utilice esta misma biblioteca como proveedor SSL._

## Requisitos

Para utilizar esta aplicación, son necesarios los siguientes componentes:

### Necesarios

- **Java 11**: Por motivos de desarrollo, este proyecto solo funciona correctamente con esta versión.
- [**post-quantum-support-kafka**](https://github.com/Ithakua/post-quantum-support-kafka): Este repositorio debe ser descargado al mismo nivel que el repositorio actual, de manera que:

```
$ ls
post-quantum-support-kafka/
custom-security-provider-kafka/
```

### Opcionales

- **IDE IntelliJ**: Al tratarse de un proyecto Java y haberse usado durante el desarrollo del proyecto, es una recomendación particular utilizar el mismo IDE para reproducir el mismo entorno.
