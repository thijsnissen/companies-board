package nl.thijsnissen.db

import java.sql.Connection

opaque type TransactionIsolationLevel <: Int = Int

object TransactionIsolationLevel:
  def apply(til: Int): TransactionIsolationLevel = til

  lazy val transactionNone: TransactionIsolationLevel            = Connection.TRANSACTION_NONE
  lazy val transactionReadUncommitted: TransactionIsolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED
  lazy val transactionReadCommitted: TransactionIsolationLevel   = Connection.TRANSACTION_READ_COMMITTED
  lazy val transactionRepeatableRead: TransactionIsolationLevel  = Connection.TRANSACTION_REPEATABLE_READ
  lazy val transactionSerializable: TransactionIsolationLevel    = Connection.TRANSACTION_SERIALIZABLE

  lazy val fromString: String => Option[TransactionIsolationLevel] =
    case "TRANSACTION_NONE"             => Some(transactionNone)
    case "TRANSACTION_READ_UNCOMMITTED" => Some(transactionReadUncommitted)
    case "TRANSACTION_READ_COMMITTED"   => Some(transactionReadCommitted)
    case "TRANSACTION_REPEATABLE_READ"  => Some(transactionRepeatableRead)
    case "TRANSACTION_SERIALIZABLE"     => Some(transactionSerializable)
    case _                              => None
