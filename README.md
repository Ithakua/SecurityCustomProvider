# Custom Security-Provider Kafka

This repository is part of a Bachelor Thesis and consists of a Java project capable of loading security providers with post-quantum cryptography support in [Apache Kafka](https://kafka.apache.org/) using the [Bouncy Castle](https://www.bouncycastle.org/) library. It also includes tools to measure handshake time once Kafka is deployed.

## What's in the Project?

The structure of the project is as follows:

- **normalProv**: This package includes the class that adds the _BouncyCastleProvider_ to Kafka.
- **pqcProv**: Package that includes the class that adds the _BouncyCastlePQCProvider_ to Kafka.
- **sslProv**: This package includes the class that adds the _BouncyCastleJSSEprovider_ to Kafka.
- **config**: Includes the `NamedGroupsConfig` class that loads the available cryptographic groups from the _BouncyCastleJSSEProvider_.
- **testHandshake**: Package that includes the tools necessary to measure the handshake time with the server for N requests, both with client authentication and without client authentication.

### Important Considerations

_The project's dependency manager is Maven, and the version of BouncyCastle used in this project is 1.78.1. If you want to change the version, simply modify the `pom.xml` and replace it with the desired version._

## How to Generate the JARs

The main purpose of this project is to generate the `.jar` files that allow the infrastructure of the [post-quantum-support-kafka](https://github.com/Ithakua/post-quantum-support-kafka) repository to implement the necessary security providers to use the cryptographic groups that enable ML-KEM communications. To do this, you need to generate a JAR from the project that includes the necessary dependencies (uber-jar).

With this JAR, if saved in the `./KafkaApp/libs` directory of the `post-quantum-support-kafka` repository, you can deploy a Kafka infrastructure with a custom security provider and a custom JSSE provider:

- **CustomSecurityProvider_mlkem.jar**: Name referenced by the Docker environment variables to deploy the Kafka server with an ML-KEM configuration.
- **CustomSecurityProvider_allgroups.jar**: Reference name for a mixed KEM configuration that includes both classic KEM groups and post-quantum KEM groups.

### Important Considerations

_The groups to be loaded in the Kafka configuration are in the `NamedGroupsConfig` class. If you want to use additional groups, you must specify them in this class._

_When working on this project and testing, it's common for some generated JARs to cause deployment issues. If an `internal_error(80) alert` is detected or there are problems in some of the handshakes when running the test, it's recommended to regenerate the JAR and reload it. If the error persists, it's recommended to reload the Maven dependencies._

## How to Measure Handshake Times

To use this functionality, you need to have the Kafka infrastructure from the `post-quantum-support-kafka` repository downloaded and deployed.

Once the Kafka server is deployed, and depending on whether the `KAFKA_SSL_CLIENT_AUTH: 'required'` parameter in the docker-compose.yaml is active, you can use two tools:

- **TestHandshake**: This tool allows testing the algorithms specified in the `NamedGroupsConfig` class, generating N (parameter to select) requests to the server and measuring the time for each group.
- **TestHandshake_clientAuth**: A modification of the previous tool that allows generating N requests to the server with client authentication.

Once all handshake agreements are completed, you can see the generated `.csv` in the `./testing` folder of the `./post-quantum-support-kafka` repository, saved as `handshake_times.csv`.

### Important Considerations

_It's important to note that these classes measure handshake time using BouncyCastle. To avoid issues, it's best to perform SSL connections with a server using this same library as the SSL provider._

## Requirements

To use this application, the following components are necessary:

### Necessary

- **Java 11**: For development reasons, this project only works correctly with this version.
- [**post-quantum-support-kafka**](https://github.com/Ithakua/post-quantum-support-kafka): This repository must be downloaded at the same level as the current repository, so that:

```sh
$ ls
post-quantum-support-kafka/
custom-security-provider-kafka/
```

### Optional

- **IDE IntelliJ**: As this is a Java project and was used during its development, it is particularly recommended to use the same IDE to reproduce the same environment.

