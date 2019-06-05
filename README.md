# money-transfers-service
A service which provides a RESTFull API to move money from accounts. 

This version is built without any framework, such a Spring or Struts. Although, we have used Hibernate to manage the data base, which will be on memory (H2). The service is executable itself, so the only thing you have to do is to generate the jar and execute it.

Good luck!

Pre-requirements
--
* Maven version: 3.6.1
* Java version: 11.0.2

Instalation
--

1) Clone the project in your local environment following the instruction below:
    
    ```
    git clone git@github.com:blecua84/money-transfers-service.git
    ```
    
2) In the terminal, from the root of the new project, execute the following instruction:
    
    ```
    mvn clean install
    ```
    
3) Once the installation is finished, in the terminal, go to the folder 'target':

    ![Alt text](screenshots/windows-01.png?raw=true "Navigation window")

4) Finally, we execute the jar in the terminal, typing the instruction below:

    ```
    java -jar money-transfers-service.jar
    ```
    
   And, at the end of the loading, we will have a service waiting for request in the port 8080:
   
   ![Alt text](screenshots/windows-02.png?raw=true "Server Ready")


Endpoints
--

The server will have the following endpoints available:

1) Create transaction 
    - **URL:** /transfers
    - **METHOD:** POST
    - **PARAMETERS:** No parameters
    - **BODY:** 
    
        ```
        {
          "from": {
            "sortCode": "010203",
            "accountNumber": "12345678"
          },
          "to": {
            "sortCode": "090129",
            "accountNumber": "12340002"
          },
          "amount": "90.50"
        }
        ```
    
    - **Success Response:** 
        - **Status:** OK
        - **Content:**  
        ```
        {
            "status": 200,
            "message": "Operation successfully executed",
            "data": null
        }
        ```
    - **Error Response:** 
        - **Status:** 400 BAD REQUEST
        - **Content:**  
        ```
        {
            "status": 400,
            "message": "Account does not exist.",
            "data": null
        }
        ```
        - **Example:** 
        ```
        curl --header "Content-Type: application/json" \
          --request POST \
          --data '{"from": {"sortCode": "010203","accountNumber": "12345678"},"to": {"sortCode": "090129","accountNumber": "12340002" }, "amount": "90.50" }' \
          http://localhost:8080/transfers
        ```

2) Get all transactions 
    - **URL:** /transfers
    - **METHOD:** GET
    - **PARAMETERS:** No parameters
    - **Success Response:** 
        - **Status:** OK
        - **Content:**  
        ```
        {
            "status": 200 OK,
            "message": "Operation successfully executed",
            "data": [
                {
                    "from": {
                        "sortCode": "010203",
                        "accountNumber": "12345678"
                    },
                    "to": {
                        "sortCode": "090129",
                        "accountNumber": "12340002"
                    },
                    "amount": "90.5"
                }
            ]
        } 
        ```
    - **Example:**
        ```
        curl http://localhost:8080/transfers
        ```

Notes:
--
1) The system does not allow to create new accounts. The service, at the beginning of its execution, will create some of them in the data base (You can find below a list of all of them):


| SORT CODE |	ACCOUNT NUMBER  |	AVAILABLE   |
| --- | --- | ---
| 010203     |       12345678	|    5000.00    |
| 010203	    |       12345680	|    20000.00   |
| 090129	    |       12340001	|    200.00     |
| 090129	    |       12340002	|    587.24     |
| 090129	    |       12340003	|    15001.00   |
| 090129	    |       12340004	|    2500.00    |
| 090129	    |       12340005	|    1230.00    |
| 090129	    |       12340006	|    50.00      |
| 090129	    |       12340007	|    5020.00    |
 

2) All operation will fail if you do not use a existing account. If you want to include another one, you will have to modify the file:
   
   ```
   src/main/resources/load-initial-data.json
   ```
   
   Where all the account that are available in the system are defined.

3) In the case of creation a new transfer, the system will return the request with 400 if any input data does not inform with the correct format.
