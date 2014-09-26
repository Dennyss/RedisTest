package templates;

import dto.InputMessage;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/18/2014.
 */
public class InputMessageTemplate extends AbstractTemplate<List<InputMessage>> {
    private static final InputMessageTemplate instance = new InputMessageTemplate();

    public static InputMessageTemplate getInstance() {
        return instance;
    }

    private InputMessageTemplate() {
    }

    @Override
    public void write(Packer packer, List<InputMessage> inputMessages, boolean required) throws IOException {
        if (inputMessages == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            packer.writeNil();
            return;
        }

        packer.writeArrayBegin(inputMessages.size());
        for(InputMessage inputMessage : inputMessages){
            packer.writeArrayBegin(4);
            packer.write(inputMessage.getVin());
            packer.write(inputMessage.getPoint().getLatitude());
            packer.write(inputMessage.getPoint().getLongitude());
            packer.write(inputMessage.getTimestamp());
            packer.writeArrayEnd();
        }
        packer.writeArrayEnd();
    }

    @Override
    public List<InputMessage> read(Unpacker unpacker, List<InputMessage> inputMessages, boolean required) throws IOException {
        return null;
    }

}
