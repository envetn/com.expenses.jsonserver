package jsonserver.common.view;

import java.util.List;

/**
 * Created by olof on 2016-07-09.
 */
public interface DeleteRequest extends Request
{
    List<String> getIdToRemove();

}
