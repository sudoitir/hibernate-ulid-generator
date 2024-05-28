# Hibernate ULID Integration

[![Java CI with Maven](https://github.com/sudoitir/hibernate-ulid-generator/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/sudoitir/hibernate-ulid-generator/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=coverage)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=bugs)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=sudoitir_hibernate-ulid-generator&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=sudoitir_hibernate-ulid-generator)


Java `ULID` Implementation
by [huxi/sulky](https://github.com/huxi/sulky/blob/master/sulky-ulid/src/main/java/de/huxhorn/sulky/ulid/ULID.java)

This project adds support for ULID (Universally Unique Lexicographically Sortable Identifier) to Hibernate, allowing
ULIDs to be used as identifiers for entity objects.

## Features

- **ULID Class**: Implementation of the ULID class providing generation and parsing functionality.
- **Custom Identifier Generator**: Creation of a custom identifier generator (ULIDGenerator) for Hibernate entities.
- **Hibernate Type Mapping**: Definition of a Hibernate type (ULIDType) to handle ULID persistence.
- **Entity Integration**: Integration of ULID support into entity mappings.

## Usage

1. **Include Dependency**: Add the ULID class and custom generator to your project.

2. **Entity Mapping**: Use the `@GeneratedValue` annotation with the custom generator and specify the ULID type for
   entity IDs.

    ```java
   import com.github.sudoitir.ulid.hibernate.ULIDType;
   import com.github.sudoitir.ulid.hibernate.generator.UlidGenerator;
   import com.github.sudoitir.ulid.hibernate.annotation.UlidGenerator;

    @Entity
    public class YourEntity {
   
        @Id
        @GeneratedValue(generator = "ulid")
        @GenericGenerator(name = "ulid", type = UlidGenerator.class)
        @Type(value = ULIDType.class)
        private ULID id;
   
        // Or
   
        @Id
        @GeneratedValue(generator = "ulid")
        @GenericGenerator(name = "ulid", type = UlidGenerator.class)
        private String id;
   
        // Or byte[]

        // Other entity properties and methods
    }
    ```

3. **Configuration**: Ensure that your Hibernate configuration recognizes the ULID type and custom generator.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or create a
pull request.

## License

This project is licensed under the MIT License.

# ULID

**Note**: This project has undergone a refactoring of the `ULID` class to update its design to be more in line with
Java's `UUID` class.
Refactored the `ULID` class to follow the design principles of Java's UUID class. This includes changes to method names
and structure to enhance readability and maintainability.
The `ULID` class provides a way to generate, parse, and manipulate ULID (Universally Unique Lexicographically Sortable
Identifier) values in Java. Below are examples of how to use the various methods provided by the `ULID` class.

## Background

A GUID/UUID can be suboptimal for many use-cases because:

- It isn't the most character efficient way of encoding 128 bits
- It provides no other information than randomness

A ULID however:

- Is compatible with UUID/GUID's
- 1.21e+24 unique ULIDs per millisecond (1,208,925,819,614,629,174,706,176 to be exact)
- Lexicographically sortable
- Canonically encoded as a 26 character string, as opposed to the 36 character UUID
- Uses Crockford's base32 for better efficiency and readability (5 bits per character)
- Case insensitive
- No special characters (URL safe)

## Install

```shell
mvn install
```

## Adding the Project as a Dependency

After installing the project locally using Maven, you can add it as a dependency in your Maven project by adding the
following XML snippet to your project's `pom.xml` file:

```xml

<dependency>
    <groupId>com.github.sudoitir</groupId>
    <artifactId>ulid-generator</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

The `ULID` class provides a way to generate, parse, and manipulate ULID (Universally Unique Lexicographically Sortable
Identifier) values in Java.
Below are examples of how to use the various methods provided by the `ULID` class.

### Creating a Random ULID

To create a new random ULID:

```java
ULID timedUlid = ULID.randomULID();
System.out.println("Random Timed ULID: " + timedUlid);
```

### Creating a Random ULID with a Specific Timestamp

```java
long timestamp = System.currentTimeMillis();
ULID timedUlid = ULID.randomULID(timestamp);
System.out.println("Random Timed ULID: " + timedUlid);
```

### Creating a ULID from Bytes

To create a ULID from a 16-byte array:

```java
byte[] ulidBytes = new byte[16];
// populate ulidBytes with your data
ULID ulidFromBytes = ULID.fromBytes(ulidBytes);
System.out.println("ULID from bytes: " + ulidFromBytes);
```

### Incrementing a ULID

To increment the least significant bits of a ULID:

```java
ULID timedUlid = ULID.randomULID();
ULID incrementedUlid = timedUlid.increment();
System.out.println("Incremented ULID: " + incrementedUlid);
```

### Generating the Next Monotonic ULID

To generate the next monotonic ULID based on a previous ULID:

```java
ULID previousUlid = ULID.randomULID();
ULID nextMonotonicUlid = previousUlid.nextMonotonicValue(previousUlid);
System.out.println("Next Monotonic ULID: " + nextMonotonicUlid);
```

### Generating the Next Monotonic ULID with a Specific Timestamp

```java
long timestamp = System.currentTimeMillis();
ULID nextMonotonicUlidWithTimestamp = ULID.nextMonotonicValue(previousUlid, timestamp);
System.out.println("Next Monotonic ULID with timestamp: " + nextMonotonicUlidWithTimestamp);
```

### Generating the Next Strictly Monotonic ULID

To generate the next strictly monotonic ULID or return empty if an overflow occurred:

```java
ULID previousUlid = ULID.randomULID();
Optional<ULID> nextStrictlyMonotonicUlid = ULID.nextStrictlyMonotonicValue(previousUlid);
nextStrictlyMonotonicUlid.ifPresent(ulid -> System.out.println("Next Strictly Monotonic ULID: " + ulid));
```

### Generating the Next Strictly Monotonic ULID with a Specific Timestamp

```java
long timestamp = System.currentTimeMillis();
Optional<ULID> nextStrictlyMonotonicUlidWithTimestamp = ULID.nextStrictlyMonotonicValue(previousUlid, timestamp);
nextStrictlyMonotonicUlidWithTimestamp.ifPresent(ulid -> System.out.println("Next Strictly Monotonic ULID with timestamp: " + ulid));
```

### Parsing a ULID String

To parse a ULID from a string representation:

```java
String ulidString = "01HYTW0TRGN5CJ7V4JN8EYN58P";
ULID parsedUlid = ULID.parseULID(ulidString);
System.out.println("Parsed ULID: " + parsedUlid);
```

### Components

**Timestamp**

- 48 bits
- UNIX-time in milliseconds
- Won't run out of space till the year 10889 AD

**Entropy**

- 80 bits
- User defined entropy source.

### Encoding

[Crockford's Base32](http://www.crockford.com/wrmg/base32.html) is used as shown.
This alphabet excludes the letters I, L, O, and U to avoid confusion and abuse.

```
0123456789ABCDEFGHJKMNPQRSTVWXYZ
```

### Binary Layout and Byte Order

The components are encoded as 16 octets. Each component is encoded with the Most Significant Byte first (network byte
order).

```
0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                      32_bit_uint_time_high                    |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|     16_bit_uint_time_low      |       16_bit_uint_random      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       32_bit_uint_random                      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       32_bit_uint_random                      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
```

### String Representation

```
 01AN4Z07BY      79KA1307SR9X4MV3
|----------|    |----------------|
 Timestamp           Entropy
  10 chars           16 chars
   48bits             80bits
   base32             base32
```

- [alizain/ulid](https://github.com/alizain/ulid)
- [huxi/sulky](https://github.com/huxi/sulky/blob/master/sulky-ulid/src/main/java/de/huxhorn/sulky/ulid/ULID.java)
