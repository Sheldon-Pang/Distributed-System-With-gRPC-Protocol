# GRPC Services and Registry

The following folder contains a Registry.jar which includes a Registering service where Nodes can register to allow clients to find them and use their implemented GRPC services. 

Additional implementation:

    1. Hometowns - This service saves classmates names and where they are from that clients add and keeps track of. 
        
        service Hometowns {
            rpc read (google.protobuf.Empty) returns (HometownsReadResponse) {}
            rpc search (HometownsSearchRequest) returns (HometownsReadResponse) {}
            rpc write (HometownsWriteRequest) returns (HometownsWriteResponse) {}
        }

        For additional detail see proto file

    2. Recipe - Users can post their own recipes that contain ingredients, their name and the recipe's name, etc.
        
        service Recipe {
            rpc addRecipe (RecipeReq) returns (RecipeResp) {}
            rpc viewRecipes (google.protobuf.Empty) returns (RecipeViewResp) {}
            rpc rateRecipe (RecipeRateReq) returns (RecipeResp) {}
        }

        For additional detail see proto file

    3. Libarary - This service saves book information, such as author, book tile, and year released.
      
         service Library {
            rpc getAll (google.protobuf.Empty) returns (LibraryGetAllResponse) {}
            rpc searchBook (LibrarySearchBookRequest) returns (LibraryGetAllResponse) {}
            rpc addBook (LibraryAddBookRequest) returns (LibraryAddBookResponse) {}
         }

        For additional detail see proto file

Before starting do a "gradle generateProto".

## How to run
Please follow the order to start each services.
```
* For Task 1: Starting the service locally
1. gradle runNode
2. gradle runClient -q --console=plain

* For Task 3: Building a network together, must start in the correct order
1. gradle runRegistryServer
2. gradle runNode -PregOn=true
3. gradle runClient -q --console=plain -PregOn=true

* For Task 3.2: Register node online at ASU ser321test.duckdns.org port 8080
1. gradle runNode -PregOn=true -PgrpcPort=8080 -PregistryHost=ser321test.duckdns.org -PnodeName=bpang6
```

### More detail on 'gradle runRegistryServer'
Will run the Registry node on localhost (arguments are possible see gradle). This node will run and allows nodes to register themselves. 

The Server allows Protobuf, JSON and gRPC. We will only be using gRPC

### More detail on 'gradle runNode'
Will run a node with an Echo and Joke service. The node registers itself on the Registry. You can change the host and port the node runs on and this will register accordingly with the Registry

### More detail on 'gradle runClient -q --console=plain'
Will run a client which will call the services from the node, it talks to the node directly not through the registry. At the end the client does some calls to the Registry to pull the services, this will be needed later.

### Video demo
```
https://drive.google.com/file/d/14Mye_UqZTW6Z02F_vwkYWXhT4PLe7bbU/view?usp=share_link
```

### Requirement Check
Task 1:
1. Checked, see above how to run
2. Checked
   1. Hometowns (read, search, write) all implemented without issue
   2. Recipe (addRecipe, viewRecipes, rateRecipe) all implemented without issue
3. Checked, server disconnect will not result in client crash, or vice versa
4. Checked, Note: slight different format, host and port set by default 
   1. Run this first, 'gradle runNode'
   2. Run this second, 'gradle runClient -q --console=plain -Pauto=1'
   3. You should be able to request service without the register server now
5. Checked, handles unexpected user inputs

Task 2: 
1. Protocol Design - library.proto 
   
   (see file for more detail, located /src/main/proto/services/library.proto)
   ```
   This service saves book information, such as author, book tile, and year released.
   1. 'searchBook', user can retrieve a specific book using book title
   2. 'addBook', user can add a book to the library
   3. 'getAll', user can get all books info from library
      ```
2. Client implementation - Checked (See video for action)
3. Server implementation - Checked (See video for action)

Task 3:
1. Everything checked, please see 'How To Run' for detail!

Task 3.2:
1. Registered on ser321test.duckdns.org, port 8080


### gradle runDiscovery
Will create a couple of threads with each running a node with services in JSON and Protobuf. This is just an example and not needed for assignment 6. 

### gradle testProtobufRegistration
Registers the protobuf nodes from runDiscovery and do some calls. 

### gradle testJSONRegistration
Registers the json nodes from runDiscovery and do some calls. 
