package com.github.xsi640.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "journal")
class Journal(
    @Id
    @SequenceGenerator(name = "journal_id_seq", sequenceName = "journal_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_id_seq")
    @Column(name = "id")
    var id: Int = 0,
    @Column
    var message: String = "",
    @Column
    var type: JournalType = JournalType.FEED,
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    var createdDate: Date = Date()
)

enum class JournalType(val code: Int) {
    FEED(1), FILTER(2), TEMP(3);

    companion object {
        fun codeOf(code: Int): JournalType {
            for (value in values()) {
                if (value.code == code) {
                    return value
                }
            }
            throw IllegalArgumentException()
        }
    }
}

@Converter(autoApply = true)
class JournalTypeConverter : AttributeConverter<JournalType, Int> {
    override fun convertToDatabaseColumn(type: JournalType): Int {
        return type.code
    }

    override fun convertToEntityAttribute(code: Int): JournalType {
        return JournalType.codeOf(code)
    }

}