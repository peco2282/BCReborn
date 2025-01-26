package peco2282.bcreborn.transport.block.pipe;

public class OverflowStrageException extends RuntimeException {
  public OverflowStrageException(int capacity, int size, String type) {
    super(String.format("Overflow %s : %d capacity %d size", type, capacity, size));
  }
}
