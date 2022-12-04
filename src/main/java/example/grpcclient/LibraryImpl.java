package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.LinkedList;

class LibraryImpl extends LibraryGrpc.LibraryImplBase {
    public LinkedList<Book> bookList = new LinkedList<>();
    @Override
    public void addBook(LibraryAddBookRequest req, StreamObserver<LibraryAddBookResponse> responseObserver) {
        String title =  req.getBook().getTitle();
        String author = req.getBook().getAuthor();
        String releaseYear =  req.getBook().getReleaseYear();
        System.out.println("Received from client: " + title + " " + author + " " + releaseYear);
        LibraryAddBookResponse.Builder response = LibraryAddBookResponse.newBuilder();

        try {
            Book.Builder addBook = Book.newBuilder();
            addBook.setTitle(title);
            addBook.setAuthor(author);
            addBook.setReleaseYear(releaseYear);

            Book finalBook = addBook.build();
            bookList.add(finalBook);
            System.out.println("Book has been saved.");
            response.setIsSuccess(true);
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setError("Unable to save Book info.");
        }

        LibraryAddBookResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void searchBook(LibrarySearchBookRequest req, StreamObserver<LibraryGetAllResponse> responseObserver) {
        String title =  req.getTitle();
        System.out.println("Received from client: " + title);
        LibraryGetAllResponse.Builder response = LibraryGetAllResponse.newBuilder();

        for(Book book : bookList) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                System.out.println("Book found.");
                response.setIsSuccess(true);
                response.addBook(book);
            }
        }

        if (!response.getIsSuccess()) {
            response.setIsSuccess(false);
            response.setError("Did not find any book match the title.");
        }

        LibraryGetAllResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void getAll(Empty req, StreamObserver<LibraryGetAllResponse> responseObserver) {
        System.out.println("Received from client: request to read all book info.");
        LibraryGetAllResponse.Builder response = LibraryGetAllResponse.newBuilder();

        for (Book book : bookList) {
            response.addBook(book);
        }

        LibraryGetAllResponse resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
