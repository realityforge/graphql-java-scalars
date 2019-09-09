package org.realityforge.graphql.scalars;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import javax.annotation.Nonnull;

final class LocalDateCoercing
  implements Coercing<LocalDate, String>
{
  @Nonnull
  @Override
  public String serialize( @Nonnull final Object input )
  {
    if ( input instanceof TemporalAccessor )
    {
      return serializeTemporalAccessor( input, (TemporalAccessor) input );
    }
    else if ( input instanceof Date )
    {
      return serializeDate( input, (Date) input );
    }
    else if ( input instanceof String )
    {
      return serializeString( (String) input );
    }
    else
    {
      final String message = "Failed to serialize value " + input + " as a LocalDate. Expected a " +
                             "type 'String', 'java.util.Date' or 'java.time.temporal.TemporalAccessor' " +
                             "but was '" + input.getClass().getName() + "'.";
      throw new CoercingSerializeException( message );
    }
  }

  @Nonnull
  private String serializeString( @Nonnull final String input )
  {
    return serializeTemporalAccessor( input, parseLocalDateToSerialize( input ) );
  }

  @Nonnull
  private String serializeDate( @Nonnull final Object input, @Nonnull final Date date )
  {
    try
    {
      return date.toInstant().atZone( ZoneId.systemDefault() ).format( DateTimeFormatter.ISO_LOCAL_DATE );
    }
    catch ( final Exception e )
    {
      throw newCoercingSerializeException( input, e );
    }
  }

  @Nonnull
  private String serializeTemporalAccessor( @Nonnull final Object input,
                                            @Nonnull final TemporalAccessor temporalAccessor )
  {
    try
    {
      return DateTimeFormatter.ISO_LOCAL_DATE.format( temporalAccessor );
    }
    catch ( final DateTimeException e )
    {
      throw newCoercingSerializeException( input, e );
    }
  }

  @Nonnull
  private LocalDate parseLocalDateToSerialize( @Nonnull final String input )
  {
    try
    {
      return parseLocalDate( input );
    }
    catch ( final DateTimeParseException e )
    {
      final String message = "Error parsing value '" + input + "' to serialize as a LocalDate. Expected to be " +
                             "in the ISO local date format, such as '2011-12-03'";
      throw new CoercingSerializeException( message, e );
    }
  }

  @Nonnull
  private CoercingSerializeException newCoercingSerializeException( @Nonnull final Object input,
                                                                    @Nonnull final Throwable e )
  {
    return new CoercingSerializeException( "Failed to serialize value " + input + " as a LocalDate.", e );
  }

  @Nonnull
  @Override
  public LocalDate parseValue( @Nonnull final Object input )
  {
    final LocalDate date = doParseValue( input );
    if ( isInvalidYear( date ) )
    {
      final String message =
        "LocalDate value " + input + " must be between January 1, 1753 and December 31, 9999. Received: " + date;
      throw new CoercingParseValueException( message );
    }
    return date;
  }

  @Nonnull
  private LocalDate doParseValue( @Nonnull final Object input )
  {
    if ( input instanceof String )
    {
      try
      {
        return parseLocalDate( (String) input );
      }
      catch ( final DateTimeParseException e )
      {
        final String message = "Error parsing value '" + input + "'. Expected to be in the ISO local date " +
                               "format, such as '2011-12-03'";
        throw new CoercingParseValueException( message );
      }
    }
    else if ( input instanceof Number )
    {
      try
      {
        return parseLocalDate( ( (Number) input ).longValue() );
      }
      catch ( final Throwable t )
      {
        final String message = "Error parsing literal '" + input + "'. Expected to be in milliseconds since Epoch";
        throw new CoercingParseValueException( message );
      }
    }
    else
    {
      final String message = "Error parsing literal " + input + " as it is the incorrect type. " +
                             "Expected a string or an integer.";
      throw new CoercingParseValueException( message );
    }
  }

  @Nonnull
  @Override
  public LocalDate parseLiteral( @Nonnull final Object input )
  {
    final LocalDate date = doParseLiteral( input );
    if ( isInvalidYear( date ) )
    {
      final String message = "LocalDate value " + input + " must be between January 1, 1753 and December 31, 9999.";
      throw new CoercingParseLiteralException( message );
    }
    return date;
  }

  @Nonnull
  private LocalDate doParseLiteral( @Nonnull final Object input )
  {
    if ( input instanceof StringValue )
    {
      final String value = ( (StringValue) input ).getValue();
      try
      {
        return parseLocalDate( value );
      }
      catch ( final Throwable t )
      {
        final String message = "Error parsing literal '" + value + "'. Expected to be in the ISO local date " +
                               "format, such as '2011-12-03'";
        throw new CoercingParseLiteralException( message );
      }
    }
    else if ( input instanceof IntValue )
    {
      final BigInteger value = ( (IntValue) input ).getValue();
      try
      {
        return parseLocalDate( value );
      }
      catch ( final Throwable t )
      {
        final String message = "Error parsing literal '" + value + "'. Expected to be in milliseconds since Epoch";
        throw new CoercingParseLiteralException( message );
      }
    }
    else
    {
      final String message = "Error parsing literal " + input + " as it is the incorrect type. " +
                             "Expected a string or an integer.";
      throw new CoercingParseLiteralException( message );
    }
  }

  @Nonnull
  private LocalDate parseLocalDate( @Nonnull final String value )
  {
    return LocalDate.from( DateTimeFormatter.ISO_LOCAL_DATE.parse( value ) );
  }

  @Nonnull
  private LocalDate parseLocalDate( @Nonnull final BigInteger value )
  {
    return parseLocalDate( value.longValue() );
  }

  @Nonnull
  private LocalDate parseLocalDate( final long epochMilli )
  {
    return LocalDate.from( Instant.ofEpochMilli( epochMilli ) );
  }

  private static boolean isInvalidYear( @Nonnull final LocalDate date )
  {
    final int year = date.getYear();
    return ( 9999 - 1753 ) <= year && year >= ( 9999 - 1900 );
  }
}
