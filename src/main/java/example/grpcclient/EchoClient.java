package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import service.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// just to show how to use the empty in the protobuf protocol
    // Empty empty = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
  private final EchoGrpc.EchoBlockingStub echoBlockingStub;
  private final JokeGrpc.JokeBlockingStub jokeBlockingStub;
  private final RegistryGrpc.RegistryBlockingStub registryBlockingStub;
  private final HometownsGrpc.HometownsBlockingStub hometownsBlockingStub;
  private final RecipeGrpc.RecipeBlockingStub recipeBlockingStub;
  private final LibraryGrpc.LibraryBlockingStub libraryBlockingStub;

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to shut it down.
    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    echoBlockingStub = EchoGrpc.newBlockingStub(channel);
    jokeBlockingStub = JokeGrpc.newBlockingStub(channel);
    hometownsBlockingStub = HometownsGrpc.newBlockingStub(channel);
    recipeBlockingStub = RecipeGrpc.newBlockingStub(channel);
    libraryBlockingStub = LibraryGrpc.newBlockingStub(channel);
    registryBlockingStub = RegistryGrpc.newBlockingStub(regChannel);
  }
  public void askServerToParrot(String message) {
    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = echoBlockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }
  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;
    try {
      response = jokeBlockingStub.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("[+]Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println(" --- " + joke);
    }
  }
  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;
    try {
      response = jokeBlockingStub.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    List<String> services = new ArrayList<>();
    try {
      response = registryBlockingStub.getServices(request);
      int numOfServices = response.getServicesCount();
      for (int i = 0; i < numOfServices; i++) {
        services.add(response.getServices(i));
      }
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    String result;
    try {
      response = registryBlockingStub.findServer(request);
      result = response.getConnection().getUri() + ":" + response.getConnection().getPort();
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = registryBlockingStub.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }
  public void writeHometown(String name, String city, String region) {
    Hometown.Builder hometown = Hometown.newBuilder();
    hometown.setName(name).setCity(city).setRegion(region);
    HometownsWriteRequest request = HometownsWriteRequest.newBuilder().setHometown(hometown).build();
    HometownsWriteResponse response;
    try {
      response = hometownsBlockingStub.write(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getIsSuccess()) {
      System.out.println("Received from server: " + response.getIsSuccess());
    } else {
      System.out.println("Received from server: " + response.getIsSuccess());
      System.out.println("Received from server: " + response.getError());
    }
  }
  public void readHometown() {
    Empty empty = Empty.newBuilder().build();
    HometownsReadResponse response;
    try {
      response = hometownsBlockingStub.read(empty);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getHometownsList().size() != 0) {
      for (Hometown hometown : response.getHometownsList()) {
        System.out.println("Found Name: " + hometown.getName() +
                ", City: " + hometown.getCity() +
                ", Region: " + hometown.getRegion()
        );
      }
    } else {
      System.out.println("No one in the record yet.");
    }
  }
  public void searchAddress(String city) {
    HometownsSearchRequest request = HometownsSearchRequest.newBuilder().setCity(city).build();
    HometownsReadResponse response;
    try {
      response = hometownsBlockingStub.search(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getHometownsList().size() != 0) {
      for (Hometown hometown : response.getHometownsList()) {
        System.out.println("Found Name: " + hometown.getName() +
                " ,City: " + hometown.getCity() +
                " ,Region: " + hometown.getRegion()
        );
      }
    } else {
      System.out.println("Did not find anyone that match the city.");
    }
  }
  public void readRecipes() {
    Empty empty = Empty.newBuilder().build();
    RecipeViewResp response;
    try {
      response = recipeBlockingStub.viewRecipes(empty);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getRecipesList().size() != 0) {
      for (RecipeEntry recipeEntry : response.getRecipesList()) {
        System.out.println("Found recipe id: " + recipeEntry.getId() +
                ", name: " + recipeEntry.getName() +
                ", author: " + recipeEntry.getAuthor() +
                ", ingredient count: " + recipeEntry.getIngredientCount() +
                ", rating: " + recipeEntry.getRating()
        );
        for (int i = 0; i < recipeEntry.getIngredientCount(); i++) {
          System.out.println(" --- " + recipeEntry.getIngredient(i));
        }
      }
    } else {
      System.out.println("No recipe in record yet.");
    }
  }
  public void addRecipe(String name, String author, Ingredient ingredient) {
    RecipeReq request = RecipeReq.newBuilder().setAuthor(author).setName(name).addIngredient(ingredient).build();
    RecipeResp response;
    try {
      response = recipeBlockingStub.addRecipe(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getIsSuccess()) {
      System.out.println("Received from server: " + response.getIsSuccess());
    } else {
      System.out.println("Received from server: " + response.getIsSuccess());
      System.out.println("Received from server: " + response.getError());
    }
  }
  public void rateRecipe(int id, float rating) {
    RecipeRateReq request = RecipeRateReq.newBuilder().setId(id).setRating(rating).build();
    RecipeResp response;
    try {
      response = recipeBlockingStub.rateRecipe(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getIsSuccess()) {
      System.out.println("Received from server: " + response.getIsSuccess());
    } else {
      System.out.println("Received from server: " + response.getIsSuccess());
      System.out.println("Received from server: " + response.getError());
    }
  }
  public void addBook(String title, String author, String releaseYear) {
    Book.Builder book = Book.newBuilder();
    book.setTitle(title).setAuthor(author).setReleaseYear(releaseYear);
    LibraryAddBookRequest request = LibraryAddBookRequest.newBuilder().setBook(book).build();
    LibraryAddBookResponse response;
    try {
      response = libraryBlockingStub.addBook(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getIsSuccess()) {
      System.out.println("Received from server: " + response.getIsSuccess());
    } else {
      System.out.println("Received from server: " + response.getIsSuccess());
      System.out.println("Received from server: " + response.getError());
    }
  }
  public void getAll() {
    Empty empty = Empty.newBuilder().build();
    LibraryGetAllResponse response;
    try {
      response = libraryBlockingStub.getAll(empty);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getBookList().size() != 0) {
      for (Book book : response.getBookList()) {
        System.out.println("Found Book: " + book.getTitle() +
                ", author: " + book.getAuthor() +
                ", release year: " + book.getReleaseYear()
        );
      }
    } else {
      System.out.println("No book in the record yet.");
    }
  }
  public void searchBook(String title) {
    LibrarySearchBookRequest request = LibrarySearchBookRequest.newBuilder().setTitle(title).build();
    LibraryGetAllResponse response;
    try {
      response = libraryBlockingStub.searchBook(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    if (response.getBookList().size() != 0) {
      for (Book book : response.getBookList()) {
        System.out.println("Found Book: " + book.getTitle() +
                " ,author: " + book.getAuthor() +
                " ,release year: " + book.getReleaseYear()
        );
      }
    } else {
      System.out.println("Did not find book that match the title.");
    }
  }


  public static void main(String[] args) throws Exception {
    if (args.length != 7) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)> <auto(int)>");
      System.exit(1);
    }
    int auto = 0;
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
      auto = Integer.parseInt(args[6]);
      if (auto > 1 || auto < 0) {
        System.out.println("[!]Error: auto args can only be 0 or 1.");
        System.exit(6);
      }
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }

    System.out.println("[+]Auto is set to: " + auto);

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe and reusable.
    // It is common to create channels at the beginning of your application and reuse them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       *
       */

      // Just doing some hard coded calls to the service node without using the registry create client
      EchoClient client = new EchoClient(channel, regChannel);
      // call the parrot service on the server
      client.askServerToParrot(message);
      // ask the user for input how many jokes the user wants
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      /* --- menu --- */
      boolean quit = false;
      while (!quit) {

        System.out.println();
        System.out.println("""
                  * Menu *
                0. Quit
                1. services.Echo/parrot         - server echo message
                2. services.Joke/setJoke        - set joke on server
                3. services.Joke/getJoke        - get joke from server
                4. services.Hometowns/write     - add hometown info
                5. services.Hometowns/read      - get all hometown info
                6. services.Hometowns/search    - search a classmate at specific city
                7. services.Recipe/addRecipe    - add a recipe
                8. services.Recipe/viewRecipes  - view all recipe
                9. services.Recipe/rateRecipe   - rate a recipe
                10.services.Registry/getServices- get all services from registry
                11.services.Registry/findServer - find server info with specific service
                12.services.Library/addBook     - add a book
                13.services.Library/searchBook  - search a book
                14.services.Library/getAll      - get all book info
                """);
        System.out.print("Enter the number to select: ");

        String input = reader.readLine();
        try {
          int userSelection = Integer.parseInt(input);
          switch (userSelection) {
            case 0:
              quit = true;
              break;
            case 1:
              System.out.print("Enter the message you want the server to echo: ");
              String messageToEcho = reader.readLine();
              client.askServerToParrot(messageToEcho);
              break;
            case 2:
              System.out.print("Enter the joke you'd like to set: ");
              String newJoke = reader.readLine();
              client.setJoke(newJoke);
              break;
            case 3:
              System.out.print("How many jokes would you like: ");
              String num = reader.readLine();
              try {
                int jokeCount = Integer.parseInt(num);
                client.askForJokes(jokeCount);
              } catch (NumberFormatException e) {
                System.out.println("[!]Error: Enter integer only.");
              }
              break;
            case 4:
              System.out.print("What is the name: ");
              String name = reader.readLine();
              System.out.print("What is the city: ");
              String city = reader.readLine();
              System.out.print("What is the region: ");
              String region = reader.readLine();
              client.writeHometown(name, city, region);
              break;
            case 5:
              client.readHometown();
              break;
            case 6:
              System.out.print("What is the city: ");
              String cityToSearch = reader.readLine();
              client.searchAddress(cityToSearch);
              break;
            case 7:
              System.out.print("Name of the recipe: ");
              String recipeName = reader.readLine();
              System.out.print("Author of the recipe: ");
              String recipeAuthor = reader.readLine();
              System.out.print("Name of the ingredient, e.g. 'garlic': ");
              String ingredientName = reader.readLine();
              System.out.print("Quantity of the ingredient in grams, e.g. '10': ");
              String ingredientQuantity = reader.readLine();
              int gram = 0;
              try {
                gram = Integer.parseInt(ingredientQuantity);
              } catch (NumberFormatException e) {
                System.out.println("[!]Error: Enter integer only.");
              }
              System.out.print("Details of the ingredient, e.g. 'mice the garlic': ");
              String ingredientDetails = reader.readLine();
              Ingredient.Builder addIngredient = Ingredient.newBuilder();
              addIngredient.setName(ingredientName);
              addIngredient.setQuantity(gram);
              addIngredient.setDetails(ingredientDetails);
              Ingredient finalIngredient = addIngredient.build();
              client.addRecipe(recipeName, recipeAuthor, finalIngredient);
              break;
            case 8:
              client.readRecipes();
              break;
            case 9:
              System.out.print("ID of the recipe (if don't know, view recipe to see available id, '-1' exit to menu): ");
              String recipeID = reader.readLine();
              int id = -1;
              try {
                id = Integer.parseInt(recipeID);
                if (id == -1) {
                  break;
                }
              } catch (NumberFormatException e) {
                System.out.println("[!]Error: Enter integer only.");
                break;
              }
              System.out.print("Your rating 0 - 5: ");
              String recipeRating = reader.readLine();
              float rating = 0;
              try {
                rating = Float.parseFloat(recipeRating);
                if (rating < 0 || rating > 5) {
                  System.out.println("[!]Error: Rating can only between 0 to 5.");
                  break;
                }
              } catch (NumberFormatException e) {
                System.out.println("[!]Error: Enter integer only.");
                break;
              }
              client.rateRecipe(id, rating);
              break;
            case 10:
              client.getServices();
              break;
            case 11:
              System.out.print("Enter name of the service to find server detail, e.g. 'services.Recipe/rateRecipe': ");
              String serviceName = reader.readLine();
              client.findServer(serviceName);
              break;
            case 12:
              System.out.print("Enter title of the book, e.g. 'Flatland': ");
              String bookTitle = reader.readLine();
              System.out.print("Enter author of the book, e.g. 'Edwin Abbott': ");
              String bookAuthor = reader.readLine();
              System.out.print("Enter year released, e.g. '1884': ");
              String bookYear = reader.readLine();
              client.addBook(bookTitle, bookAuthor, bookYear);
              break;
            case 13:
              System.out.print("Enter title of the book to search, e.g. 'Flatland': ");
              bookTitle = reader.readLine();
              client.searchBook(bookTitle);
              break;
            case 14:
              client.getAll();
              break;
            default:
              System.out.println("[!]Please make a valid selection between: 0 - 11.");
          }
        } catch (NumberFormatException e) {
          System.out.println("[!]Error: Enter integer only.");
        }
      }

    } catch (Exception e) {
      System.out.println("[!]Error: Server offline.");
    } finally {
      // ManagedChannels use resources like threads and TCP connections.
      // To prevent leaking these resources the channel should be shut down when it will no longer be used.
      // If it may be used again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

}
