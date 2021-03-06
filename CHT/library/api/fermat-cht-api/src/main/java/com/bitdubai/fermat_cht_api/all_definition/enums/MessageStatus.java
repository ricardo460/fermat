package com.bitdubai.fermat_cht_api.all_definition.enums;

import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEnum;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;

/**
 * Created by franklin on 08/01/16.
 */
public enum MessageStatus implements FermatEnum {
    /**
     * Definition types
     */
    CREATED   ("CRET"),
    SEND      ("SEND"),
    DELIVERED ("DELR"),
    READ      ("READ");

    /**
     * Represent the code of the message status
     */
    private final String code;

    /**
     * Constructor whit parameter
     *
     * @param code the valid code
     */
    MessageStatus(String code) {
        this.code = code;
    }
    /**
     * Throw the method <code>getCode</code> you can get the code of the specific element of the enum.
     *
     * @return the code of the enum.
     */
    @Override
    public String getCode() {
        return this.code ;
    }

    /**
     * Return the enum by the code
     *
     * @param code the valid code
     * @return MessagesStatus enum
     * @throws InvalidParameterException error with is no a valid code
     */
    public static MessageStatus getByCode(String code) throws InvalidParameterException {

        switch (code) {
            case "CRET":
                return MessageStatus.CREATED;
            case "SEND":
                return MessageStatus.SEND;
            case "DELR":
                return MessageStatus.DELIVERED;
            case "READ":
                return MessageStatus.READ;
        }

        /**
         * If we try to convert am invalid string.
         */
        throw new InvalidParameterException(code);
    }
}
