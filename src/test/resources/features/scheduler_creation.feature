Feature: Is scheduler created ?
  Test the scheduler creation

  Scenario Outline: Scheduler is created as per configuration
    Given for the following job configurations
      | job_id | schedule_type | job_day             | is_active | nth_day |
      | 1      | daily         | null                | true      | null    |
      | 2      | weekly        | tue,wed,thu,fri,sat | true      | null    |
      | 3      | weekly        | tue                 | true      | null    |
      | 4      | weekly        | mon,tue             | true      | null    |
      | 5      | monthly       | null                | true      | 03      |
      | 6      | daily         | null                | false     | null    |
      | 7      | weekly        | null                | false     | 04      |
      | 8      | yearly        | null                | false     | 0425    |

    When scheduler is created for "<order_date>"
    Then only "<job_ids_created>" should be created

    Examples:
      | order_date | job_ids_created |
      | 2024-03-31 | 1,4             |
      | 2024-04-01 | 1,2,3,4         |
      | 2024-04-02 | 1,2,5           |

