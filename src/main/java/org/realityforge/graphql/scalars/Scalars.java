package org.realityforge.graphql.scalars;

import graphql.schema.GraphQLScalarType;

public final class Scalars
{
  /**
   * A scalar that formats/parses a date-time without an offset, such as '2011-12-03T10:15:30'.
   */
  public static GraphQLScalarType LOCAL_DATE_TIME =
    GraphQLScalarType.newScalar()
      .name( "LocalDateTime" )
      .description( "An ISO-8601 extended offset date-time format Scalat" )
      .coercing( new LocalDateTimeCoercing() )
      .build();
  /**
   * A scalar that formats/parses a date without an offset, such as '2011-12-03'.
   */
  public static GraphQLScalarType LOCAL_DATE =
    GraphQLScalarType.newScalar()
      .name( "LocalDate" )
      .description( "An ISO-8601 extended local date format Scalar" )
      .coercing( new LocalDateCoercing() )
      .build();

  private Scalars()
  {
  }
}
