package org.runaway.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

public class Invoices {

    public static SendInvoice.SendInvoiceBuilder invoiceDonate(long chat_ID) {
        return SendInvoice.builder().chatId(String.valueOf(chat_ID)).title("Донатик").description("Поддержите разработчика, он старался!").payload("test")
                .providerToken("401643678:TEST:d4ea9f50-9bda-4ea2-bcd1-03fde2ccb7c4").currency("RUB").price(new LabeledPrice("Донат", 149)).suggestedTipAmount(500);
    }
}
