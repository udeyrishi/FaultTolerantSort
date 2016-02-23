import java.util.*;

/**
 * Created by rishi on 2016-02-20.
 */
public class RandomlyFailingList<E> implements List<E> {
    private final List<E> elements;
    private final RandomlyFailingOperationWrapper operationWrapper;

    public RandomlyFailingList(List<E> innerList, double failureProbability) throws IllegalArgumentException {
        elements = innerList;
        operationWrapper = new RandomlyFailingOperationWrapper(failureProbability);
    }

    @Override
    public int size() {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.size();
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.isEmpty();
            }
        });
    }

    @Override
    public boolean contains(final Object o) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.contains(o);
            }
        });
    }

    @Override
    public Iterator<E> iterator() {
        ArrayList<Integer> a = new ArrayList<>();
        return new RandomlyFailingIterator();
    }

    @Override
    public Object[] toArray() {
        // Can't fail this, as it's a primitive array
        return elements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // Can't fail this, as it's a primitive array
        return elements.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.add(e);
            }
        });
    }

    @Override
    public boolean remove(final Object o) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.remove(o);
            }
        });
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.containsAll(c);
            }
        });
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.addAll(c);
            }
        });
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.addAll(index, c);
            }
        });
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.removeAll(c);
            }
        });
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return operationWrapper.execute(new Operation<Boolean>() {
            @Override
            public Boolean execute() {
                return elements.retainAll(c);
            }
        });
    }

    @Override
    public void clear() {
        operationWrapper.execute(new Operation<Void>() {
            @Override
            public Void execute() {
                elements.clear();
                return null;
            }
        });
    }

    @Override
    public E get(final int index) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.get(index);
            }
        });
    }

    @Override
    public E set(final int index, final E element) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.set(index, element);
            }
        });
    }

    @Override
    public void add(final int index, final E element) {
        operationWrapper.execute(new Operation<Void>() {
            @Override
            public Void execute() {
                elements.add(index, element);
                return null;
            }
        });
    }

    @Override
    public E remove(final int index) {
        return operationWrapper.execute(new Operation<E>() {
            @Override
            public E execute() {
                return elements.remove(index);
            }
        });
    }

    @Override
    public int indexOf(final Object o) {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.indexOf(o);
            }
        });
    }

    @Override
    public int lastIndexOf(final Object o) {
        return operationWrapper.execute(new Operation<Integer>() {
            @Override
            public Integer execute() {
                return elements.lastIndexOf(o);
            }
        });
    }

    @Override
    public ListIterator<E> listIterator() {
        return new RandomlyFailingListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new RandomlyFailingListIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return operationWrapper.execute(new Operation<List<E>>() {
            @Override
            public List<E> execute() {
                return elements.subList(fromIndex, toIndex);
            }
        });
    }

    private static class RandomlyFailingOperationWrapper {
        private final double failureProbability;
        private long count = 0;

        public RandomlyFailingOperationWrapper(double failureProbability) throws IllegalArgumentException {
            if (failureProbability > 1.0 || failureProbability < 0.0) {
                throw new IllegalArgumentException("Failure Probability needs to be between 0 and 1.");
            }

            this.failureProbability = failureProbability;
        }

        public <T> T execute(Operation<T> operation) {
            ++count;
            if (shouldFail()) {
                throw new MemoryAccessFailureException("Random simulated failure event.");
            }

            return operation.execute();
        }

        private boolean shouldFail() {
            double hazard = count * failureProbability;
            Random rand = new Random();
            double randomEvent = rand.nextDouble();
            return randomEvent >= 0.5 && randomEvent <= 0.5 + hazard;
        }
    }

    private class RandomlyFailingIterator implements Iterator<E> {
        private final Iterator<E> iterator;

        public RandomlyFailingIterator() {
            this.iterator = elements.iterator();
        }

        @Override
        public boolean hasNext() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return iterator.hasNext();
                }
            });
        }

        @Override
        public E next() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return iterator.next();
                }
            });
        }

        @Override
        public void remove() {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    iterator.remove();
                    return null;
                }
            });
        }
    }

    private class RandomlyFailingListIterator implements ListIterator<E> {

        private final ListIterator<E> listIterator;

        public RandomlyFailingListIterator(int index) {
            if (index < 0 || index > elements.size()) {
                throw new IndexOutOfBoundsException("Index: " + index);
            }
            this.listIterator = elements.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return listIterator.hasNext();
                }
            });
        }

        @Override
        public E next() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return listIterator.next();
                }
            });
        }

        @Override
        public boolean hasPrevious() {
            return operationWrapper.execute(new Operation<Boolean>() {
                @Override
                public Boolean execute() {
                    return listIterator.hasPrevious();
                }
            });
        }

        @Override
        public E previous() {
            return operationWrapper.execute(new Operation<E>() {
                @Override
                public E execute() {
                    return listIterator.previous();
                }
            });
        }

        @Override
        public int nextIndex() {
            return operationWrapper.execute(new Operation<Integer>() {
                @Override
                public Integer execute() {
                    return listIterator.nextIndex();
                }
            });
        }

        @Override
        public int previousIndex() {
            return operationWrapper.execute(new Operation<Integer>() {
                @Override
                public Integer execute() {
                    return listIterator.previousIndex();
                }
            });
        }

        @Override
        public void remove() {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    listIterator.remove();
                    return null;
                }
            });
        }

        @Override
        public void set(final E e) {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    listIterator.set(e);
                    return null;
                }
            });
        }

        @Override
        public void add(final E e) {
            operationWrapper.execute(new Operation<Void>() {
                @Override
                public Void execute() {
                    listIterator.add(e);
                    return null;
                }
            });
        }
    }
}
