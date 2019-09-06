package org.realityforge.graphql.scalars;

import graphql.schema.GraphQLScalarType;

public final class Scalars
{
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
