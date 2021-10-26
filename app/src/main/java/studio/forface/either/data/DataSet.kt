package studio.forface.either.data

import studio.forface.either.data.model.ContactDataModel
import studio.forface.either.data.model.MessageDataModel
import studio.forface.either.domain.ARMOR
import studio.forface.either.domain.model.EncryptedContact
import studio.forface.either.domain.model.EncryptedMessage

object DataSet {

    object Contacts {

        val all get() =
            listOf(Davide, Zorica, Marino, Stefanija, Maciej)

        val Davide = ContactDataModel(
            id = 1,
            name = "${ARMOR}Davide$ARMOR",
            email = "${ARMOR}davide@email.com$ARMOR"
        )
        val Zorica = ContactDataModel(
            id = 2,
            name = "${ARMOR}Zorica$ARMOR",
            email = "${ARMOR}zorica@email.com$ARMOR"
        )
        val Marino = ContactDataModel(
            id = 3,
            name = "${ARMOR}Marino$ARMOR",
            email = "${ARMOR}marino@email.com$ARMOR"
        )
        val Stefanija = ContactDataModel(
            id = 4,
            name = "${ARMOR}Stefanija$ARMOR",
            email = "${ARMOR}stefanija@email.com$ARMOR"
        )
        val Maciej = ContactDataModel(
            id = 5,
            name = "${ARMOR}Maciej$ARMOR",
            email = "${ARMOR}maciej@email.com$ARMOR"
        )
    }

    object Messages {

        val all = listOf(
            MessageDataModel(
                id = 1,
                subject = "${ARMOR}Your sandwich is ready$ARMOR",
                fromId = Contacts.Davide.id
            ),
            MessageDataModel(
                id = 2,
                subject = "${ARMOR}Do you want to eat pizza tonight?$ARMOR",
                fromId = Contacts.Zorica.id
            ),
            MessageDataModel(
                id = 3,
                subject = "${ARMOR}Am I hungry?$ARMOR",
                fromId = Contacts.Maciej.id
            ),
            MessageDataModel(
                id = 4,
                subject = "${ARMOR}Why do I only write things about food?$ARMOR",
                fromId = Contacts.Stefanija.id
            ),
            MessageDataModel(
                id = 5,
                subject = "${ARMOR}Let's talk about technology$ARMOR",
                fromId = Contacts.Marino.id
            ),
            MessageDataModel(
                id = 6,
                subject = "${ARMOR}Di you hear about new MacBooks?$ARMOR",
                fromId = Contacts.Zorica.id
            ),
            MessageDataModel(
                id = 7,
                subject = "${ARMOR}NEW MACBOOKS FOR SALE!!!$ARMOR",
                fromId = Contacts.Maciej.id
            ),
            MessageDataModel(
                id = 8,
                subject = "${ARMOR}Why is Maciej selling MacBooks?$ARMOR",
                fromId = Contacts.Stefanija.id
            ),
            MessageDataModel(
                id = 9,
                subject = "${ARMOR}PIZZA FOR SALE!!!$ARMOR",
                fromId = Contacts.Marino.id
            )
        )
    }
}
