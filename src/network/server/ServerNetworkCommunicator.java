package network.server;

import network.NetworkCommunicator;
import network.networkMessageHandler.NetworkMsgHandler;
import network.networkMessages.NetworkMsg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerNetworkCommunicator extends NetworkCommunicator{

  protected ServerSocket serverSocket = null;

  private List<ClientWorker> clientWorkers;

  public ServerNetworkCommunicator(Map<Integer, NetworkMsgHandler> msgHandlers){
    super(msgHandlers);
    this.clientWorkers = new ArrayList<>();
  }

  public void run(){
    System.out.println("Starting server on port: " + port);
    synchronized(this){
      this.runningThread = Thread.currentThread();
    }
    openServerSocket();
    while(! isStopped()){
      Socket clientSocket = null;
      try {
        clientSocket = this.serverSocket.accept();
        setUpIncomingClient(clientSocket);
      } catch (IOException e) {
        if(isStopped()) {
          System.out.println("ServerNetworkCommunicator Stopped.") ;
          return;
        }
        throw new RuntimeException(
                "Error accepting client connection", e);
      }
    }
    System.out.println("ServerNetworkCommunicator Stopped.") ;
  }

  public synchronized void stop(){
    this.isStopped = true;
    try {
      System.out.println("Stopping ServerNetworkCommunicator");
      for (ClientWorker worker: clientWorkers) {
        worker.stop();
      }
      this.serverSocket.close();
    } catch (IOException e) {
      throw new RuntimeException("Error closing server", e);
    }
  }

  private void openServerSocket() {
    try {
      this.serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      throw new RuntimeException("Cannot open port " + port, e);
    }
  }

  private void setUpIncomingClient(Socket clientSocket){
    ClientWorker worker = new ClientWorker(clientSocket, "Multithreaded ServerNetworkCommunicator", msgHandlers);
    clientWorkers.add(worker);
    Thread incomingClientWorkerThread = new Thread(worker);
    incomingClientWorkerThread.start();
    //long id = incomingClientWorkerThread.getId();
    //worker.id = id;
  }

  public void sendMsgToAllClients(NetworkMsg msg) {
    for (ClientWorker worker: clientWorkers) {
      worker.send(msg);
    }
  }

  @Override
  protected void send(NetworkMsg networkMsg) {
    sendMsgToAllClients(networkMsg);
  }
}