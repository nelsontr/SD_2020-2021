# Guião de Demonstração

(incompleto -- **compete ao grupo completar o guião** -- ver TODOs)

## 1. Preparação do sistema

Para testar o sistema e todos os seus componentes, é necessário preparar um ambiente com dados para proceder à verificação dos testes.

### 1.1. Lançar o *registry*

> **NOTA :** Não se encontra implementado nesta fase to projeto, estará brevemente disponível. Saltar esta secção .

Para lançar o *ZooKeeper*, ir à pasta `zookeeper/bin` e correr o comando  
`./zkServer.sh start` (Linux) ou `zkServer.cmd` (Windows).

É possível também lançar a consola de interação com o *ZooKeeper*, novamente na pasta `zookeeper/bin` e correr `./zkCli.sh` (Linux) ou `zkCli.cmd` (Windows).

### 1.2. Compilar o projeto

Primeiramente, é necessário compilar e instalar todos os módulos e suas dependências --  *rec*, *hub*, *app*, etc.
Para isso, basta ir à pasta *root* do projeto e correr o seguinte comando:

```sh
$ mvn clean install -DskipTests
```

### 1.3. Lançar e testar o *rec*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *rec* .
Para isso basta ir à pasta *rec* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *rec* no endereço *localhost* e na porta *8091*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd rec-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.


### 1.4. Lançar e testar o *hub*

Para proceder aos testes, é preciso em primeiro lugar lançar o servidor *hub* .
Para isso basta ir à pasta *hub* e executar:

```sh
$ mvn compile exec:java
```

Este comando vai colocar o *hub* no endereço *localhost* e na porta *8081*.

Para confirmar o funcionamento do servidor com um *ping*, fazer:

```sh
$ cd hub-tester
$ mvn compile exec:java
```

Para executar toda a bateria de testes de integração, fazer:

```sh
$ mvn verify
```

Todos os testes devem ser executados sem erros.

### 1.5. *App*

Iniciar a aplicação com a utilizadora alice:

```sh
$ app localhost 8081 joao +35191102030 38.7380 -9.3000
```

**Nota:** Para poder correr o script *app* diretamente é necessário fazer `mvn install` e adicionar ao *PATH* ou utilizar diretamente os executáveis gerados na pasta `target/appassembler/bin/`.

Abrir outra consola, e iniciar a aplicação com o utilizador maria.

Depois de lançar todos os componentes, tal como descrito acima, já temos o que é necessário para usar o sistema através dos comandos.

## 2. Teste dos comandos

Nesta secção vamos correr os comandos necessários para testar todas as operações do sistema.
Cada subsecção é respetiva a cada operação presente no *hub*.

> **Nota Importante**
> * Quando aparece '>' após a linha executada, significa que executou e está a espera de mais comandos;
> * Quando aparece '$' após a linha executada, significa que executou e saiu do programa.

### 2.1. *balance*

```sh
#Forma correta (utilizador existente)
> balance
joao 0 BIC
>

#Forma incorreta (utilizador não registado)
> balance
io.grpc.StatusRuntimeException: NOT_FOUND: User does not exist in records
Caught exception with description: User does not exist in records
$

#Forma incorreta (utilizador registado)
> balance 2
--balance Format is 'balance'
>
```

### 2.2 *top-up*

```sh
#Forma correta (utilizador existente)
> top-up 5
joao 50 BIC
>

#Forma incorreta (sem args)
> top-up
--top-up Format is 'top-up %int%'
>

#Forma incorreta (>20 || <1)
> top-up 30
io.grpc.StatusRuntimeException: INVALID_ARGUMENT: Stake has to be in range [1, 20]!
Caught exception with description: Stake has to be in range [1, 20]!
$

#Com user não registado no users.cvs
> top-up 5
io.grpc.StatusRuntimeException: NOT_FOUND: UserName not registered!
Caught exception with description: UserName not registered!
$
```

### 2.3 *tag*

```sh
#Forma correta
> tag 38.0000 -9.3000 loc1
OK
>

#Forma incorreta (sem args)
> tag
--tag Format is 'tag %latitude% %longitude% %name%'
>

#Forma incorreta
> tag 38.0000 -9.3000 
--tag Format is 'tag %latitude% %longitude% %name%'
>
```

### 2.4 *move*

```sh
#Forma correta (1)
> move 38.0000 -9.3000
joao em https://www.google.com/maps/place/38.0,-9.3
>

#Forma correta (2)
> move loc1
joao em https://www.google.com/maps/place/38.0,-9.3
>

#Forma incorreta (sem args)
> move
--move Format is 'move %name%' or 'move %latitude% %longitude%'
>

#Forma incorreta (não existe tag)
> move loc2
--tag Format is 'tag %latitude% %longitude% %name%'
io.grpc.StatusRuntimeException: ILLEGAL_ARGUMENTS: tag does not exist!
Caught exception with description: tag does not exist!
$
```

### 2.5 *at*

```sh
#Forma correta
> at
joao em https://www.google.com/maps/place/38.738,-9.3
>

#Forma incorreta (sem args)
> at 1
--at Format is 'at'
>
```

### 2.5 *scan*

```sh
#Forma correta (1)
> scan 3
istt, lat38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 157 metros
stao, lat38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 5750 metros
jero, lat38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 7096 metros
>

#Forma correta (2)
#Se numero>NUMERO_ESTACOES, retorna NUMERO_ESTACOES
> scan 12
istt, lat38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, a 157 metros
stao, lat38.6867, -9.3124 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 5750 metros
jero, lat38.6972, -9.2064 long, 30 docas, 3 BIC prémio, 20 bicicletas, a 7096 metros
...
>

#Forma incorreta (sem args)
> scan
--scan Format is 'scan %int%'
>
```

### 2.6 *info*

```sh
#Forma correta (1)
> info istt
IST Taguspark, lat 38.7372, -9.3023 long, 20 docas, 4 BIC prémio, 12 bicicletas, 0 levantamentos, 0 devoluções, https://www.google.com/maps/place/38.7372,-9.3023
>

#Forma incorreta (stationId demasiado grande)
> info isosos
io.grpc.StatusRuntimeException: INVALID_ARGUMENT: Station Id must be 4 letters long
Caught exception with description: Station Id must be 4 letters long
$

#Forma incorreta (nao existe stationId)
> info isos
//TODO
$

#Forma incorreta (sem args)
> info
--info Format is 'info %id%'
>
```

### 2.7 *bike-up*

```sh
#Forma correta (1)
> bike-up istt
OK
>

#Forma incorreta (demasiado longe)
> bike-up jero
ERRO Out of Reach
>

#Forma incorreta (no stationId)
> bike-up unkown
io.grpc.StatusRuntimeException: UNKNOWN
Caught exception with description: null
$

#Forma incorreta (sem args)
> bike-up
--bike-up Format is 'bike-up %id%'
>
```

### 2.8 *bike-down*

```sh
#Forma correta (1)
> bike-down istt
OK
>

#Forma incorreta (demasiado longe)
> bike-down stao
ERRO Out of Reach
>

> bike-down unkown
io.grpc.StatusRuntimeException: UNKNOWN
Caught exception with description: null
$

#Forma incorreta (sem args)
> bike-down
--bike-down Format is 'bike-down %id%'
>
```

### 2.9 *sys_status*

```sh
#Forma correta
> sys_status
Hub is: UP. Rec is: UP
>

#Forma incorreta
> sys_status 2
--sys_status Format is 'sys_status'
>
```

### 2.10 *Comandos extra*

```sh
#--- ping ---
#Forma correta
> ping "nelson"
Hello "nelson"
>

#--- help ---
#Forma correta
> help
--ping Format is 'ping %message%' 
--balance Format is 'balance'
--top-up Format is 'top-up %int%'
--tag Format is 'tag %latitude% %longitude% %name%'
--move Format is 'move %name%'
--move Format is 'move %latitude% %longitude%'
--at Format is 'at'
--scan Format is 'scan %int%'
--info Format is 'info %id%'
--bike-up Format is 'bike-up %id%'
--bike-down Format is 'bike-down %id%'
--sys_status Format is 'sys_status'
--quit Format is 'quit'
>

#--- quit ---
#Forma correta
> quit
$

#--- zzz ---
#Forma correta
> zzz 150
Interrupted Exception!
Slept for: 150 ms
>
```

----

## 3. Considerações Finais

Estes testes não cobrem tudo, pelo que devem ter sempre em conta os testes de integração e o código.
