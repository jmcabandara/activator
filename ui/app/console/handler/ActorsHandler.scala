/**
 * Copyright (C) 2013 Typesafe <http://typesafe.com/>
 */
package console
package handler

import akka.actor.{ ActorRef, Props }
import console.handler.rest.ActorsJsonBuilder.ActorsResult
import activator.analytics.data._
import console.handler.rest.ActorsJsonBuilder.ActorsResult
import activator.analytics.repository.ActorStatsSorted
import console.PagingInformation
import scala.Some
import console.ScopeModifiers
import activator.analytics.rest.http.SortingHelpers.SortDirection

object ActorsHandler {
  case class ActorsModuleInfo(scope: Scope,
    modifiers: ScopeModifiers,
    time: TimeRange,
    pagingInformation: Option[PagingInformation],
    sortOn: ActorStatsSort,
    sortDirection: SortDirection,
    dataFrom: Option[Long],
    traceId: Option[String]) extends MultiValueModuleInformation[ActorStatsSort]

  def extractSortOn(sortCommand: Option[String]): ActorStatsSort = sortCommand match {
    case Some(sort) ⇒ sort match {
      case "deviation" ⇒ ActorStatsSorts.DeviationsSort
      case "maxTimeInMailbox" ⇒ ActorStatsSorts.MaxTimeInMailboxSort
      case "maxMailboxSize" ⇒ ActorStatsSorts.MaxMailboxSizeSort
      case "actorPath" ⇒ ActorStatsSorts.ActorPath
      case "actorName" ⇒ ActorStatsSorts.ActorName
      case _ ⇒ ActorStatsSorts.ProcessedMessagesSort
    }
    case _ ⇒ ActorStatsSorts.ProcessedMessagesSort
  }
}

trait ActorsHandlerBase extends PagingRequestHandler[ActorStatsSort, ActorsHandler.ActorsModuleInfo] {
  import ActorsHandler._
  import SortDirections._

  def useActorStats(sender: ActorRef, stats: ActorStatsSorted): Unit

  def onModuleInformation(sender: ActorRef, mi: ActorsModuleInfo): Unit = withPagingDefaults(mi) { (offset, limit) =>
    useActorStats(sender,
      repository.actorStatsRepository.findSorted(mi.time,
        mi.scope,
        mi.modifiers.anonymous,
        mi.modifiers.temporary,
        offset,
        limit,
        mi.sortOn,
        mi.sortDirection.toLegacy))
  }
}

class ActorsHandler(builderProps: Props, val defaultLimit: Int) extends ActorsHandlerBase {
  val builder = context.actorOf(builderProps, "actorsBuilder")

  def useActorStats(sender: ActorRef, stats: ActorStatsSorted): Unit = {
    builder ! ActorsResult(sender, stats)
  }
}
